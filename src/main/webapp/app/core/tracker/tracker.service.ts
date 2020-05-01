import { Injectable } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Observable, Observer, Subscription } from 'rxjs';
import { Location } from '@angular/common';

import { CSRFService } from '../auth/csrf.service';
import { AuthServerProvider } from '../auth/auth-jwt.service';

import * as SockJS from 'sockjs-client';
import * as Stomp from 'webstomp-client';
import { JhiAlertService } from 'ng-jhipster';

@Injectable({ providedIn: 'root' })
export class JhiTrackerService {
  stompClient = null;
  subscriber = null;
  subscriber2 = null;
  connection: Promise<any>;
  connectedPromise: any;
  listener: Observable<any>;
  listenerObserver: Observer<any>;
  alreadyConnectedOnce = false;
  private subscription: Subscription;

  constructor(
    private router: Router,
    private authServerProvider: AuthServerProvider,
    private location: Location,
    private csrfService: CSRFService,
    protected jhiAlertService: JhiAlertService
  ) {
    this.connection = this.createConnection();
    this.listener = this.createListener();
  }

  connect() {
    if (this.connectedPromise === null) {
      this.connection = this.createConnection();
    }
    // building absolute path so that websocket doesn't fail when deploying with a context path
    let url = '/websocket/tracker';
    url = this.location.prepareExternalUrl(url);
    const authToken = this.authServerProvider.getToken();
    if (authToken) {
      url += '?access_token=' + authToken;
    }
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);
    const headers = {};
    this.stompClient.connect(headers, () => {
      this.connectedPromise('success');
      this.connectedPromise = null;
      this.sendActivity();
      if (!this.alreadyConnectedOnce) {
        this.subscription = this.router.events.subscribe(event => {
          if (event instanceof NavigationEnd) {
            this.sendActivity();
          }
        });
        this.alreadyConnectedOnce = true;
      }
    });
  }

  disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
      this.stompClient = null;
    }
    if (this.subscription) {
      this.subscription.unsubscribe();
      this.subscription = null;
    }
    this.alreadyConnectedOnce = false;
  }

  receive() {
    return this.listener;
  }

  sendActivity() {
    if (this.stompClient !== null && this.stompClient.connected) {
      this.stompClient.send(
        '/topic/activity', // destination
        JSON.stringify({ page: this.router.routerState.snapshot.url }), // body
        {} // header
      );
    }
  }

  subscribe() {
    this.connection.then(() => {
      this.subscriber = this.stompClient.subscribe('/topic/tracker', data => {
        this.listenerObserver.next(JSON.parse(data.body));
      });

    });
  }

  subscribeUser() {
    this.connection.then(() => {
      // Subscribe system notifications
      this.subscriber2 = this.stompClient.subscribe('/topic/user', data => {
        const payload = JSON.parse(data.body);
        if (payload) {
          if (payload.success) {
            this.jhiAlertService.success('Order with identifier ' + payload.data + ' placed successfully.');
          } else {
            this.jhiAlertService.error('Order with identifier ' + payload.data + ' failed. Please try again!');
          }
        }
      });
    });
  }


  unsubscribe() {
    if (this.subscriber !== null) {
      this.subscriber.unsubscribe();
    }
    this.listener = this.createListener();
  }

  private createListener(): Observable<any> {
    return new Observable(observer => {
      this.listenerObserver = observer;
    });
  }

  private createConnection(): Promise<any> {
    return new Promise((resolve, reject) => (this.connectedPromise = resolve));
  }
}

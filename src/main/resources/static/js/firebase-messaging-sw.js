// firebase-messaging-sw.js (Firebase 9.x.x 호환)
self.addEventListener('push', function(event) {
    const data = event.data.json();
    console.log('Push received:', data);

    const options = {
        body: data.notification.body,
        icon: '/images/logo-static.png',
        badge: '/images/logo-static.png'
    };

    event.waitUntil(
        self.registration.showNotification(data.notification.title, options)
    );
});

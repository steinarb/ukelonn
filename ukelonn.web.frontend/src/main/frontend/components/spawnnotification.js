export function spawnNotification(notification) {
    var options = {
        body: notification.message,
    };
    new Notification(notification.title, options);
}

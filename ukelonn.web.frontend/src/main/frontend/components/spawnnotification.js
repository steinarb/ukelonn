export function spawnNotification(notification) {
    var options = {
        body: notification.message,
    };
    var n = new Notification(notification.title, options);
}

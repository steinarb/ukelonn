export function isAdmin(action) {
    const payload = action.payload || {};
    const roles = payload.roles || [];
    return roles.indexOf("ukelonnadmin") !== -1;
}

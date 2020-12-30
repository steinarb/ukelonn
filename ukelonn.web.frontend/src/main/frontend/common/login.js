export function userIsNotLoggedIn(props) {
    let { haveReceivedResponseFromLogin, loginResponse } = props;
    return haveReceivedResponseFromLogin && loginResponse.roles.length === 0;
}

export function findUsername(state) {
    const loginResponse = state.loginResponse || {};
    return loginResponse.username || '';
}

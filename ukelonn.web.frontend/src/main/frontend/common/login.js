export function userIsLoggedIn(props) {
    let { haveReceivedResponseFromLogin, loginResponse } = props;
    return haveReceivedResponseFromLogin && loginResponse.roles.length === 0;
}

export function findUsernameFromAccountOrQueryParameter(props, queryParams) {
    const { account } = props;
    if (account !== undefined && account.firstName !== "Ukjent") {
        return account.username; // account in redux has priority over query parameters
    }

    return queryParams.get('username');
}

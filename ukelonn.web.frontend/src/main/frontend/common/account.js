import { parse } from 'qs';

export function findUsernameFromAccountOrQueryParameter(props) {
    const { account } = props;
    if (account !== undefined && account.firstName !== "Ukjent") {
        return account.username; // account in redux has priority over query parameters
    }

    const queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    return queryParams.username;
}

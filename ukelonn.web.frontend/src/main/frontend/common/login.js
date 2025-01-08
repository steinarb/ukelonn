import { api } from '../api';

export function findUsername(state) {
    const loginResponse = api.endpoints.getLogin.select()(state).data || {};
    return loginResponse.username || '';
}

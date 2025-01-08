import React from 'react';
import { useSelector } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostLogoutMutation,
} from '../api';

export default function Logout() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const [ postLogout ] = usePostLogoutMutation();
    const onLogoutClicked = async () => postLogout();

    return (<button onClick={onLogoutClicked}>{text.logout}</button>);
}

import { useGetLoginQuery, useGetNotificationQuery, api } from '../api';

export default function Notifier() {
    const { data: loginResponse = {}, isSuccess: loginIsSuccess } = useGetLoginQuery();
    useGetNotificationQuery(loginResponse.username, {
        skip: !loginIsSuccess,
        pollingInterval: 60000,
        refetchOnMountOrArgChange: true,
    });

    // No rendering in this component
    //
    // The purpose of the Notifier component to trigger RTK queries
    // to the notificiation endpoint
    return null;
}

import React from 'react';
import { Navigate } from 'react-router';
import { useGetLoginQuery } from '../api';

export default function Home() {
    const { data: loginResponse = { roles: [] } } = useGetLoginQuery();
    if (loginResponse.roles.length > 0) {
        if (loginResponse.roles[0] === 'ukelonnadmin') {
            return <Navigate to="/admin" />;
        }

        return <Navigate to="/user" />;
    }

    return (
        <div>
            <h1>Ukelønn hjem</h1>
        </div>
    );
}

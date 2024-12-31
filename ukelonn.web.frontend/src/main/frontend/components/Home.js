import React from 'react';
import { Navigate } from 'react-router';
import { useSelector } from 'react-redux';

export default function Home() {
    const loginResponse = useSelector(state => state.loginResponse);
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

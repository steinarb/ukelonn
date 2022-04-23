import React from 'react';
import { Redirect } from 'react-router';
import { useSelector } from 'react-redux';

export default function Home() {
    const loginResponse = useSelector(state => state.loginResponse);
    if (loginResponse.roles.length > 0) {
        if (loginResponse.roles[0] === 'ukelonnadmin') {
            return <Redirect to="/ukelonn/admin" />;
        }

        return <Redirect to="/ukelonn/user" />;
    }

    return (
        <div>
            <h1>Ukelønn hjem</h1>
        </div>
    );
}

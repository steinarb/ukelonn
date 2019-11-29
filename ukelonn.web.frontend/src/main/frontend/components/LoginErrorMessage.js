import React from 'react';

function LoginErrorMessage(props) {
    const { loginResponse } = props;
    if (loginResponse.roles.length === 0) {
        return (<h1 className="alert alert-primary">{loginResponse.errorMessage}</h1>);
    }

    return null;
}

export default LoginErrorMessage;

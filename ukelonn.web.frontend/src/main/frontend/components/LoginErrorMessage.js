import React from 'react';

let LoginErrorMessage = ({loginResponse}) => {
    if (loginResponse.roles.length === 0) {
        return (<h1 className="alert alert-primary">{loginResponse.errorMessage}</h1>);
    }
};

export default LoginErrorMessage;

import React from 'react';

let LoginErrorMessage = ({loginResponse}) => {
    if (loginResponse.roles.length === 0) {
        return (<h1>{loginResponse.errorMessage}</h1>);
    }
};

export default LoginErrorMessage;

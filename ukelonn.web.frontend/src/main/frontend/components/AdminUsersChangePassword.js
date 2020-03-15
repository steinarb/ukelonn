import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    USERS_REQUEST,
    UPDATE_USER,
    UPDATE_PASSWORDS,
    MODIFY_USER_PASSWORD_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

function AdminUsersChangePassword(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        users,
        usersMap,
        user,
        passwords,
        onUsersFieldChange,
        onPassword1Change,
        onPassword2Change,
        onSaveUpdatedPassword,
        onLogout,
    } = props;

    const reduceHeaderRowPadding = { padding: '0 0 0 0' };

    const passwordInputClass = 'mdl-textfield mdl-js-textfield stretch-to-fill' + (passwords.passwordsNotIdentical ? ' is-invalid is-dirty' : '');

    return (
        <div className="mdl-layout mdl-layout--fixed-header">
            <header className="mdl-layout__header">
                <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                    <Link to="/ukelonn/admin/users" className="mdl-navigation__link">
                        <i className="material-icons" >chevron_left</i>
                        &nbsp;
                        Administer brukere
                    </Link>
                    <span className="mdl-layout-title">Bytt passord på bruker</span>
                </div>
            </header>
            <main className="mdl-layout__content">
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="users">Velg bruker</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <Users id="users" className="stretch-to-fill" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="password1">Passord:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="password1" className="stretch-to-fill" type='password' value={passwords.password1} onChange={(event) => onPassword1Change(event.target.value)} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="password2">Gjenta passord:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <div className={passwordInputClass}>
                                <input id="password2" type='password' className="mdl-textfield__password stretch-to-fill" value={passwords.password2} onChange={(event) => onPassword2Change(event.target.value)} />
                                { passwords.passwordsNotIdentical && <span className='mdl-textfield__error is-invalid'>Passordene er ikke identiske</span> }
                            </div>
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--hide-phone mdl-cell--4-col-tablet mdl-cell--8-col-desktop">
                            &nbsp;
                        </div>
                        <div className="mdl-cell mdl-cell--4-col-phone mdl-cell--4-col-tablet mdl-cell--4-col-desktop">
                            <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onSaveUpdatedPassword(user, passwords)}>Endre passord</button>
                        </div>
                    </div>
                </form>
            </main>
            <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onLogout()}>Logout</button>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        users: state.users,
        user: state.user,
        passwords: state.passwords,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onUserList: () => dispatch(USERS_REQUEST()),
        onUsersFieldChange: (selectedValue, users) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let user = users.find(u => u.userid === selectedValueInt);
            dispatch(UPDATE_USER({ ...user }));
        },
        onPassword1Change: (password1) => dispatch(UPDATE_PASSWORDS({ password1 })),
        onPassword2Change: (password2) => dispatch(UPDATE_PASSWORDS({ password2 })),
        onSaveUpdatedPassword: (user, passwords) => dispatch(MODIFY_USER_PASSWORD_REQUEST({ user, passwords })),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersChangePassword);

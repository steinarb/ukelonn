import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    UPDATE_USER,
    MODIFY_USER_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

function AdminUsersModify(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        user,
        users,
        onUsersFieldChange,
        onUsernameChange,
        onEmailChange,
        onFirstnameChange,
        onLastnameChange,
        onSaveUpdatedUser,
        onLogout,
    } = props;

    const reduceHeaderRowPadding = { padding: '0 0 0 0' };

    return (
        <div className="mdl-layout mdl-layout--fixed-header">
            <header className="mdl-layout__header">
                <div className="mdl-layout__header-row" style={reduceHeaderRowPadding}>
                    <Link to="/ukelonn/admin/users" className="mdl-navigation__link">
                        <i className="material-icons" >chevron_left</i>
                        &nbsp;
                        Administer brukere
                    </Link>
                    <span className="mdl-layout-title">Endre brukere</span>
                </div>
            </header>
            <main className="mdl-layout__content">
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="users">Velg bruker</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <Users id="users" className="stretch-to-fill" value={user.userid} users={users} onUsersFieldChange={onUsersFieldChange} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="username">Brukernavn</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="username" className="mdl-textfield__input stretch-to-fill" type="text" value={user.username} onChange={(event) => onUsernameChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="email">Epostadresse</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="email" className="mdl-textfield__input stretch-to-fill" type="text" value={user.email} onChange={(event) => onEmailChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="firstname">Fornavn</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="firstname" className="mdl-textfield__input stretch-to-fill" type="text" value={user.firstname} onChange={(event) => onFirstnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="lastname">Etternavn</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="lastname" className="mdl-textfield__input stretch-to-fill" type="text" value={user.lastname} onChange={(event) => onLastnameChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--hide-phone mdl-cell--4-col-tablet mdl-cell--8-col-desktop">
                            &nbsp;
                        </div>
                        <div className="mdl-cell mdl-cell--4-col-phone mdl-cell--4-col-tablet mdl-cell--4-col-desktop">
                            <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onSaveUpdatedUser(user)}>Lagre endringer av bruker</button>
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
        user: state.user,
        users: state.users,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onUsersFieldChange: (selectedValue, users) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let user = users.find(u => u.userid === selectedValueInt);
            dispatch(UPDATE_USER({ ...user }));
        },
        onUsernameChange: (username) => dispatch(UPDATE_USER({ username })),
        onEmailChange: (email) => dispatch(UPDATE_USER({ email })),
        onFirstnameChange: (firstname) => dispatch(UPDATE_USER({ firstname })),
        onLastnameChange: (lastname) => dispatch(UPDATE_USER({ lastname })),
        onSaveUpdatedUser: (user) => {
            const { userid, username, email, firstname, lastname } = user;
            dispatch(MODIFY_USER_REQUEST({ userid, username, email, firstname, lastname }));
        },
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersModify);

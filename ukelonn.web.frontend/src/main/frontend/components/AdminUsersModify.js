import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    USERS_REQUEST,
    UPDATE,
    MODIFY_USER_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

class AdminUsersModify extends Component {
    componentDidMount() {
        this.props.onUserList();
    }

    render() {
        if (userIsNotLoggedIn(this.props)) {
            return <Redirect to="/ukelonn/login" />;
        }

        let {
            users,
            usersMap,
            user,
            onUsersFieldChange,
            onFieldChange,
            onSaveUpdatedUser,
            onLogout,
        } = this.props;

        return (
            <div>
                <h1>Endre brukere</h1>
                <br/>
                <Link to="/ukelonn/admin/users">Administer brukere</Link>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="users">Velg bruker</label>
                    <Users id="users" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                    <br/>
                    <label htmlFor="username">Brukernavn</label>
                    <input id="username" type="text" value={user.username} onChange={(event) => onFieldChange({username: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="email">Epostadresse</label>
                    <input id="email" type="text" value={user.email} onChange={(event) => onFieldChange({email: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="firstname">Fornavn</label>
                    <input id="firstname" type="text" value={user.firstname} onChange={(event) => onFieldChange({firstname: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="lastname">Etternavn</label>
                    <input id="lastname" type="text" value={user.lastname} onChange={(event) => onFieldChange({lastname: event.target.value}, user)} />
                    <br/>
                    <button onClick={() => onSaveUpdatedUser(user)}>Lagre endringer av bruker</button>
                </form>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
                <br/>
                <a href="../../../..">Tilbake til topp</a>
            </div>
        );
    };
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        users: state.users,
        usersMap: new Map(state.users.map(i => [i.fullname, i])),
        user: state.user,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUserList: () => dispatch(USERS_REQUEST()),
        onUsersFieldChange: (selectedValue, usersMap) => {
            let user = usersMap.get(selectedValue);
            let changedField = {
                user: {...user},
            };
            dispatch(UPDATE(changedField));
        },
        onFieldChange: (formValue, user) => {
            let changedField = {
                user: { ...user, ...formValue }
            };
            dispatch(UPDATE(changedField));
        },
        onSaveUpdatedUser: (user) => dispatch(MODIFY_USER_REQUEST(user)),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

AdminUsersModify = connect(mapStateToProps, mapDispatchToProps)(AdminUsersModify);

export default AdminUsersModify;

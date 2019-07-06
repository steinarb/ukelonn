import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
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
        let {
            haveReceivedResponseFromLogin,
            loginResponse,
            users,
            usersMap,
            user,
            onUsersFieldChange,
            onFieldChange,
            onSaveUpdatedUser,
            onLogout,
        } = this.props;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <Link className="btn btn-block btn-primary mb-0 left-align-cell" to="/ukelonn/admin/users">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    Administer brukere
                </Link>
                <header>
                    <div className="pb-2 mt-0 mb-2 border-bottom bg-light">
                        <h1>Endre brukere</h1>
                    </div>
                </header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="container">
                        <div className="form-group row">
                            <label htmlFor="users" className="col-form-label col-5">Velg bruker</label>
                            <div className="col-7">
                                <Users id="users" className="form-control" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="username" className="col-form-label col-5">Brukernavn</label>
                            <div className="col-7">
                                <input id="username" className="form-control" type="text" value={user.username} onChange={(event) => onFieldChange({username: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="email" className="col-form-label col-5">Epostadresse</label>
                            <div className="col-7">
                                <input id="email" className="form-control" type="text" value={user.email} onChange={(event) => onFieldChange({email: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="firstname" className="col-form-label col-5">Fornavn</label>
                            <div className="col-7">
                                <input id="firstname" className="form-control" type="text" value={user.firstname} onChange={(event) => onFieldChange({firstname: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="lastname" className="col-form-label col-5">Etternavn</label>
                            <div className="col-7">
                                <input id="lastname" className="form-control" type="text" value={user.lastname} onChange={(event) => onFieldChange({lastname: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <div className="col-5"/>
                            <div className="col-7">
                                <button className="btn btn-primary" onClick={() => onSaveUpdatedUser(user)}>Lagre endringer av bruker</button>
                            </div>
                        </div>
                    </div>
                    <br/>
                </form>
                <br/>
                <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
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

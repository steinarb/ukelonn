import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Users from './Users';
import Amount from './Amount';

class AdminUsersChangePassword extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onUserList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let {
            haveReceivedResponseFromLogin,
            loginResponse,
            users,
            usersMap,
            user,
            passwords,
            onUsersFieldChange,
            onPasswordsFieldChange,
            onSaveUpdatedPassword,
            onLogout,
        } = this.state;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

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
                                <label htmlFor="password">Passord:</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="password" className="stretch-to-fill" type='password' value={passwords.password} onChange={(event) => onPasswordsFieldChange({ password: event.target.value }, passwords)} />
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="password2">Gjenta passord:</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="password2" className="stretch-to-fill" type='password' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)} />
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
    };
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        users: state.users,
        usersMap: new Map(state.users.map(i => [i.fullname, i])),
        user: state.user,
        passwords: state.passwords,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUserList: () => dispatch({ type: 'USERS_REQUEST' }),
        onUsersFieldChange: (selectedValue, usersMap) => {
            let user = usersMap.get(selectedValue);
            let changedField = {
                user: {...user},
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onPasswordsFieldChange: (formValue, passwords) => {
            let changedField = {
                passwords: { ...passwords, ...formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveUpdatedPassword: (user, passwords) => dispatch({ type: 'MODIFY_USER_PASSWORD_REQUEST', user, passwords }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminUsersChangePassword = connect(mapStateToProps, mapDispatchToProps)(AdminUsersChangePassword);

export default AdminUsersChangePassword;

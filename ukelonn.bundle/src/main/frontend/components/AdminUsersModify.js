import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Users from './Users';
import Amount from './Amount';

class AdminUsersModify extends Component {
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
            onUsersFieldChange,
            onFieldChange,
            onSaveUpdatedUser,
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
                                <Users id="users" className="stretch-to-fill" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="username">Brukernavn</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="username" className="stretch-to-fill" type="text" value={user.username} onChange={(event) => onFieldChange({username: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="email">Epostadresse</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="email" className="stretch-to-fill" type="text" value={user.email} onChange={(event) => onFieldChange({email: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="firstname">Fornavn</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="firstname" className="stretch-to-fill" type="text" value={user.firstname} onChange={(event) => onFieldChange({firstname: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="lastname">Etternavn</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="lastname" className="stretch-to-fill" type="text" value={user.lastname} onChange={(event) => onFieldChange({lastname: event.target.value}, user)} />
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
        onUserList: () => dispatch({ type: 'USERS_REQUEST' }),
        onUsersFieldChange: (selectedValue, usersMap) => {
            let user = usersMap.get(selectedValue);
            let changedField = {
                user: {...user},
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onFieldChange: (formValue, user) => {
            let changedField = {
                user: { ...user, ...formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveUpdatedUser: (user) => dispatch({ type: 'MODIFY_USER_REQUEST', user }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminUsersModify = connect(mapStateToProps, mapDispatchToProps)(AdminUsersModify);

export default AdminUsersModify;

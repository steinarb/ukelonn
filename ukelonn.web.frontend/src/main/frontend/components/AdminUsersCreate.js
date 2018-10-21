import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { isEmail } from 'validator';
import Users from './Users';
import Amount from './Amount';

class AdminUsersCreate extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onClearUserAndPassword();
        this.props.onUserList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let {
            haveReceivedResponseFromLogin,
            loginResponse,
            usernames,
            user,
            passwords,
            passwordsNotIdentical,
            onUserFieldChange,
            onPasswordsFieldChange,
            onSaveCreatedUser,
            onLogout,
        } = this.state;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        const usernameEmpty = !user.username;
        const usernameExists = usernames.indexOf(user.username) > -1;
        const emailIsNotValid = user.email && !isEmail(user.email);
        const usernameInputClass = 'mdl-textfield mdl-js-textfield' + (usernameEmpty || usernameExists ? ' is-invalid is-dirty' : '');
        const emailInputClass = 'mdl-textfield mdl-js-textfield' + (emailIsNotValid ? ' is-invalid is-dirty' : '');
        const passwordInputClass = 'mdl-textfield mdl-js-textfield  stretch-to-fill' + (passwordsNotIdentical ? ' is-invalid is-dirty' : '');

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
                        <span className="mdl-layout-title">Legg til ny bruker</span>
                    </div>
                </header>
                <main className="mdl-layout__content">
                    <form onSubmit={ e => { e.preventDefault(); }}>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="username">Brukernavn</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <div className={usernameInputClass}>
                                    <input id="username" className='mdl-textfield__input stretch-to-fill' type="text" value={user.username} onChange={(event) => onUserFieldChange({username: event.target.value}, user)} />
                                    { usernameEmpty && <span className='mdl-textfield__error'>Brukernavn kan ikke være tomt</span> }
                                    { usernameExists && <span className='mdl-textfield__error'>Brukernavnet finnes fra før</span> }
                                </div>
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="email">Epostadresse</label>
                            </div>
                            <div className='mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop'>
                                <div className={emailInputClass}>
                                    <input id="email" className='mdl-textfield__input stretch-to-fill' type="text" value={user.email} onChange={(event) => onUserFieldChange({email: event.target.value}, user)} />
                                    { emailIsNotValid && <span className='mdl-textfield__error'>Ikke en gyldig epostadresse</span> }
                                </div>
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="firstname">Fornavn</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="firstname" className='mdl-textfield__input stretch-to-fill' type="text" value={user.firstname} onChange={(event) => onUserFieldChange({firstname: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="lastname">Etternavn</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="lastname" className='mdl-textfield__input stretch-to-fill' type="text" value={user.lastname} onChange={(event) => onUserFieldChange({lastname: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="password">Passord:</label>
                            </div>
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                                <input id="password" className='mdl-textfield__password stretch-to-fill' type='password' value={passwords.password} onChange={(event) => onPasswordsFieldChange({ password: event.target.value }, passwords)} />
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                                <label htmlFor="password2">Gjenta passord:</label>
                            </div>
                            <div className='mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop'>
                                <div className={passwordInputClass}>
                                    <input id="password2" type='password' className='mdl-textfield__password stretch-to-fill' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)} />
                                    { passwordsNotIdentical && <span className='mdl-textfield__error is-invalid'>Passordene er ikke identiske</span> }
                                </div>
                            </div>
                        </div>
                        <div className="mdl-grid hline-bottom">
                            <div className="mdl-cell mdl-cell--hide-phone mdl-cell--4-col-tablet mdl-cell--8-col-desktop">
                                &nbsp;
                            </div>
                            <div className="mdl-cell mdl-cell--4-col-phone mdl-cell--4-col-tablet mdl-cell--4-col-desktop">
                                <button className="mdl-button mdl-js-button mdl-button--raised" onClick={() => onSaveCreatedUser(user, passwords)}>Lag bruker</button>
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
        user: state.user,
        passwords: state.passwords,
        passwordsNotIdentical: state.passwordsNotIdentical,
        usernames: state.usernames,
    };
};

const checkIfPasswordsAreNotIdentical = (passwords) => {
    let { password, password2 } = passwords;
    if (!password2) {
        return false; // if second password is empty we don't compare because it probably hasn't been typed into yet
    }

    return password !== password2;
};

const mapDispatchToProps = dispatch => {
    return {
        onClearUserAndPassword: () => {
            dispatch({ type: 'CLEAR_USER_AND_PASSWORD' });
        },
        onUserList: () => dispatch({ type: 'USERS_REQUEST' }),
        onUserFieldChange: (formValue, user) => {
            let changedField = {
                user: { ...user, ...formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onPasswordsFieldChange: (formValue, passwordsFromState) => {
            const passwords = { ...passwordsFromState, ...formValue };
            const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(passwords);
            let changedField = {
                passwords,
                passwordsNotIdentical,
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveCreatedUser: (user, passwords) => dispatch({ type: 'CREATE_USER_REQUEST', user, passwords }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminUsersCreate = connect(mapStateToProps, mapDispatchToProps)(AdminUsersCreate);

export default AdminUsersCreate;

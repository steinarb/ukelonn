import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { isEmail } from 'validator';
import { userIsNotLoggedIn } from '../common/login';
import {
    CLEAR_USER_AND_PASSWORD,
    UPDATE_USER,
    UPDATE_PASSWORDS,
    CREATE_USER_REQUEST,
    LOGOUT_REQUEST,
} from '../actiontypes';
import Users from './Users';
import Amount from './Amount';

function AdminUsersCreate(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let {
        usernames,
        user,
        passwords,
        passwordsNotIdentical,
        onUsernameChange,
        onEmailChange,
        onFirstnameChange,
        onLastnameChange,
        onPassword1Change,
        onPassword2Change,
        onSaveCreatedUser,
        onLogout,
    } = props;

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
                                <input id="username" className="mdl-textfield__input stretch-to-fill" type="text" value={user.username} onChange={(event) => onUsernameChange(event.target.value)} />
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
                                <input id="email" className="mdl-textfield__input stretch-to-fill" type="text" value={user.email} onChange={(event) => onEmailChange(event.target.value)} />
                                { emailIsNotValid && <span className='mdl-textfield__error'>Ikke en gyldig epostadresse</span> }
                            </div>
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
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="password1">Passord:</label>
                        </div>
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop">
                            <input id="password1" className="mdl-textfield__password stretch-to-fill" type='password' value={passwords.password1} onChange={(event) => onPassword1Change(event.target.value)} />
                        </div>
                    </div>
                    <div className="mdl-grid hline-bottom">
                        <div className="mdl-cell mdl-cell--2-col-phone mdl-cell--3-col-tablet mdl-cell--3-col-desktop">
                            <label htmlFor="password2">Gjenta passord:</label>
                        </div>
                        <div className='mdl-cell mdl-cell--2-col-phone mdl-cell--5-col-tablet mdl-cell--9-col-desktop'>
                            <div className={passwordInputClass}>
                                <input id="password2" type="password" className="mdl-textfield__password stretch-to-fill" value={passwords.password2} onChange={(event) => onPassword2Change(event.target.value)}/>
                                { passwords.passwordsNotIdentical && <span className='mdl-textfield__error is-invalid'>Passordene er ikke identiske</span> }
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
}

function mapStateToProps(state) {
    return {
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        user: state.user,
        passwords: state.passwords,
        usernames: state.usernames,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onClearUserAndPassword: () => {
            dispatch(CLEAR_USER_AND_PASSWORD());
        },
        onUsernameChange: (username) => dispatch(UPDATE_USER({ username })),
        onEmailChange: (email) => dispatch(UPDATE_USER({ email })),
        onFirstnameChange: (firstname) => dispatch(UPDATE_USER({ firstname })),
        onLastnameChange: (lastname) => dispatch(UPDATE_USER({ lastname })),
        onPassword1Change: (password1) => dispatch(UPDATE_PASSWORDS({ password1 })),
        onPassword2Change: (password2) => dispatch(UPDATE_PASSWORDS({ password2 })),
        onSaveCreatedUser: (user, passwords) => dispatch(CREATE_USER_REQUEST({ user, passwords })),
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminUsersCreate);

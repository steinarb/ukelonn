import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Users from './Users';
import Amount from './Amount';

class AdminUsersCreate extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onClearUserAndPassword();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let {
            haveReceivedResponseFromLogin,
            loginResponse,
            user,
            passwords,
            onUserFieldChange,
            onPasswordsFieldChange,
            onSaveCreatedUser,
            onLogout,
        } = this.state;

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
                        <h1>Legg til ny bruker</h1>
                    </div>
                </header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="container">
                        <div className="form-group row">
                            <label htmlFor="username" className="col-form-label col-5">Brukernavn</label>
                            <div className="col-7">
                                <input id="username" className="form-control" type="text" value={user.username} onChange={(event) => onUserFieldChange({username: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="email" className="col-form-label col-5">Epostadresse</label>
                            <div className="col-7">
                                <input id="email" className="form-control" type="text" value={user.email} onChange={(event) => onUserFieldChange({email: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="firstname" className="col-form-label col-5">Fornavn</label>
                            <div className="col-7">
                                <input id="firstname" className="form-control" type="text" value={user.firstname} onChange={(event) => onUserFieldChange({firstname: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="lastname" className="col-form-label col-5">Etternavn</label>
                            <div className="col-7">
                                <input id="lastname" className="form-control" type="text" value={user.lastname} onChange={(event) => onUserFieldChange({lastname: event.target.value}, user)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="password" className="col-form-label col-5">Passord:</label>
                            <div className="col-7">
                                <input id="password" className="form-control" type='password' value={passwords.password} onChange={(event) => onPasswordsFieldChange({ password: event.target.value }, passwords)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="password2" className="col-form-label col-5">Gjenta passord:</label>
                            <div className="col-7">
                                <input id="password2" className="form-control" type='password' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)} />
                            </div>
                        </div>
                        <div className="form-group row">
                            <div className="col-5"/>
                            <div className="col-7">
                                <button className="btn btn-primary" onClick={() => onSaveCreatedUser(user, passwords)}>Lag bruker</button>
                            </div>
                        </div>
                    </div>
                </form>
                <br/>
                <button className="btn btn-default" onClick={() => onLogout()}>Logout</button>
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
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onClearUserAndPassword: () => {
            dispatch({ type: 'CLEAR_USER_AND_PASSWORD' });
        },
        onUserFieldChange: (formValue, user) => {
            let changedField = {
                user: { ...user, ...formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onPasswordsFieldChange: (formValue, passwords) => {
            let changedField = {
                passwords: { ...passwords, ...formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveCreatedUser: (user, passwords) => dispatch({ type: 'CREATE_USER_REQUEST', user, passwords }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminUsersCreate = connect(mapStateToProps, mapDispatchToProps)(AdminUsersCreate);

export default AdminUsersCreate;
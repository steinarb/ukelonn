import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SELECT_USER } from '../actiontypes';
import { emptyUser } from '../constants';

function Users(props) {
    const { id, className } = props;
    const users = useSelector(state => state.users);
    const userid = useSelector(state => state.userid);
    const dispatch = useDispatch();

    return (
        <select id={id} className={className} onChange={e => dispatch(SELECT_USER(findSelectedUser(e, users)))} value={userid}>
            <option key="user_-1" value="-1" />
            {users.map((val) => <option key={'user_' + val.userid} value={val.userid}>{val.firstname + ' ' + val.lastname}</option>)}
        </select>
    );
}

export default Users;

function findSelectedUser(e, users) {
    const selectedUserId = parseInt(e.target.value);
    return users.find(u => u.userid === selectedUserId) || emptyUser;
}

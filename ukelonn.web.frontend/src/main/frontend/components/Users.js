import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SELECT_USER } from '../actiontypes';

function Users(props) {
    const {id, className } = props;
    const users = useSelector(state => state.users);
    const userid = useSelector(state => state.userid);
    const dispatch = useDispatch();

    return (
        <select id={id} className={className} onChange={e => dispatch(SELECT_USER(parseInt(e.target.value)))} value={userid}>
            <option key="user_-1" value="-1" />
            {users.map((val) => <option key={'user_' + val.userid} value={val.userid}>{val.fullname}</option>)}
        </select>
    );
}

export default Users;

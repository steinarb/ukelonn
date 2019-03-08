import React from 'react';

const Users = ({id, className, users, usersMap, value, onUsersFieldChange }) => (
    <select id={id} className={className} onChange={(event) => onUsersFieldChange(event.target.value, usersMap)} value={value}>
        {users.map((val) => <option key={val.userid}>{val.fullname}</option>)}
    </select>
);

export default Users;

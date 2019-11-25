import React from 'react';

function Users(props) {
    const {id, className, users, usersMap, value, onUsersFieldChange } = props;
    return (
        <select id={id} className={className} onChange={(event) => onUsersFieldChange(event.target.value, usersMap)} value={value}>
          {users.map((val) => <option key={val.userid}>{val.fullname}</option>)}
        </select>
    );
}

export default Users;

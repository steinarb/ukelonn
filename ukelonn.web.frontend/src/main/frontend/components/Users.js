import React from 'react';

function Users(props) {
    const {id, className, users, value, onUsersFieldChange } = props;
    return (
        <select id={id} className={className} onChange={(event) => onUsersFieldChange(event.target.value, users)} value={value}>
          {users.map((val) => <option key={val.userid} value={val.userid}>{val.fullname}</option>)}
        </select>
    );
}

export default Users;

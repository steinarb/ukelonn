import React from 'react';
import { Routes, Route } from 'react-router';
import { HistoryRouter as Router } from "redux-first-history/rr6";
import Home from './Home';
import Login from './Login';
import Unauthorized from './Unauthorized';
import User from './User';
import PerformedJobs from './PerformedJobs';
import PerformedPayments from './PerformedPayments';
import Statistics from './Statistics';
import StatisticsEarningsSumOverYear from './StatisticsEarningsSumOverYear';
import StatisticsEarningsSumOverMonth from './StatisticsEarningsSumOverMonth';
import Admin from './Admin';
import AdminJobtypes from './AdminJobtypes';
import AdminJobtypesModify from './AdminJobtypesModify';
import AdminJobtypesCreate from './AdminJobtypesCreate';
import AdminJobsDelete from './AdminJobsDelete';
import AdminJobsEdit from './AdminJobsEdit';
import AdminPaymenttypes from './AdminPaymenttypes';
import AdminPaymenttypesModify from './AdminPaymenttypesModify';
import AdminPaymenttypesCreate from './AdminPaymenttypesCreate';
import AdminUsers from './AdminUsers';
import AdminUsersModify from './AdminUsersModify';
import AdminUsersCreate from './AdminUsersCreate';
import AdminBonusModify from './AdminBonusModify';
import AdminBonusCreate from './AdminBonusCreate';
import AdminBonusDelete from './AdminBonusDelete';
import AdminBonuses from './AdminBonuses';
import AdminUsersChangePassword from './AdminUsersChangePassword';


function App(props) {
    const { history, basename } = props;
    return(
        <Router history={history} basename={basename}>
            <Routes>
                <Route exact path="/" element={<Home/>} />
                <Route path="/login" element={<Login/>} />
                <Route path="/unauthorized" element={<Unauthorized/>} />
                <Route path="/user" element={<User/>} />
                <Route path="/performedjobs" element={<PerformedJobs/>} />
                <Route path="/performedpayments" element={<PerformedPayments/>} />
                <Route path="/statistics/earnings/sumoveryear" element={<StatisticsEarningsSumOverYear/>} />
                <Route path="/statistics/earnings/sumovermonth" element={<StatisticsEarningsSumOverMonth/>} />
                <Route path="/statistics" element={<Statistics/>} />
                <Route path="/admin/jobtypes/modify" element={<AdminJobtypesModify/>} />
                <Route path="/admin/jobtypes/create" element={<AdminJobtypesCreate/>} />
                <Route path="/admin/jobtypes" element={<AdminJobtypes/>} />
                <Route path="/admin/jobs/delete" element={<AdminJobsDelete/>} />
                <Route path="/admin/jobs/edit" element={<AdminJobsEdit/>} />
                <Route path="/admin/paymenttypes/modify" element={<AdminPaymenttypesModify/>} />
                <Route path="/admin/paymenttypes/create" element={<AdminPaymenttypesCreate/>} />
                <Route path="/admin/paymenttypes" element={<AdminPaymenttypes/>} />
                <Route path="/admin/users/password" element={<AdminUsersChangePassword/>} />
                <Route path="/admin/users/modify" element={<AdminUsersModify/>} />
                <Route path="/admin/users/create" element={<AdminUsersCreate/>} />
                <Route path="/admin/users" element={<AdminUsers/>} />
                <Route path="/admin/bonuses/modify" element={<AdminBonusModify/>} />
                <Route path="/admin/bonuses/create" element={<AdminBonusCreate/>} />
                <Route path="/admin/bonuses/delete" element={<AdminBonusDelete/>} />
                <Route path="/admin/bonuses" element={<AdminBonuses/>} />
                <Route path="/admin" element={<Admin/>} />
            </Routes>
        </Router>
    );
}

export default App;

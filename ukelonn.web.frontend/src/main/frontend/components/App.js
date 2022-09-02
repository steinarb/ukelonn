import React from 'react';
import { Routes, Route } from 'react-router-dom';
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
    const { history } = props;
    return(
        <Router history={history}>
            <Routes>
                <Route exact path="/ukelonn/" element={<Home/>} />
                <Route path="/ukelonn/login" element={<Login/>} />
                <Route path="/ukelonn/unauthorized" element={<Unauthorized/>} />
                <Route path="/ukelonn/user" element={<User/>} />
                <Route path="/ukelonn/performedjobs" element={<PerformedJobs/>} />
                <Route path="/ukelonn/performedpayments" element={<PerformedPayments/>} />
                <Route path="/ukelonn/statistics/earnings/sumoveryear" element={<StatisticsEarningsSumOverYear/>} />
                <Route path="/ukelonn/statistics/earnings/sumovermonth" element={<StatisticsEarningsSumOverMonth/>} />
                <Route path="/ukelonn/statistics" element={<Statistics/>} />
                <Route path="/ukelonn/admin/jobtypes/modify" element={<AdminJobtypesModify/>} />
                <Route path="/ukelonn/admin/jobtypes/create" element={<AdminJobtypesCreate/>} />
                <Route path="/ukelonn/admin/jobtypes" element={<AdminJobtypes/>} />
                <Route path="/ukelonn/admin/jobs/delete" element={<AdminJobsDelete/>} />
                <Route path="/ukelonn/admin/jobs/edit" element={<AdminJobsEdit/>} />
                <Route path="/ukelonn/admin/paymenttypes/modify" element={<AdminPaymenttypesModify/>} />
                <Route path="/ukelonn/admin/paymenttypes/create" element={<AdminPaymenttypesCreate/>} />
                <Route path="/ukelonn/admin/paymenttypes" element={<AdminPaymenttypes/>} />
                <Route path="/ukelonn/admin/users/password" element={<AdminUsersChangePassword/>} />
                <Route path="/ukelonn/admin/users/modify" element={<AdminUsersModify/>} />
                <Route path="/ukelonn/admin/users/create" element={<AdminUsersCreate/>} />
                <Route path="/ukelonn/admin/users" element={<AdminUsers/>} />
                <Route path="/ukelonn/admin/bonuses/modify" element={<AdminBonusModify/>} />
                <Route path="/ukelonn/admin/bonuses/create" element={<AdminBonusCreate/>} />
                <Route path="/ukelonn/admin/bonuses/delete" element={<AdminBonusDelete/>} />
                <Route path="/ukelonn/admin/bonuses" element={<AdminBonuses/>} />
                <Route path="/ukelonn/admin" element={<Admin/>} />
            </Routes>
        </Router>
    );
}

export default App;

import React from 'react';
import { Switch, Route } from 'react-router-dom';
import { ConnectedRouter as Router } from 'connected-react-router';
import Home from './Home';
import Login from './Login';
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
            <Switch>
                <Route exact path="/ukelonn/" component={Home} />
                <Route path="/ukelonn/login*" component={Login} />
                <Route path="/ukelonn/user" component={User} />
                <Route path="/ukelonn/performedjobs" component={PerformedJobs} />
                <Route path="/ukelonn/performedpayments" component={PerformedPayments} />
                <Route path="/ukelonn/statistics/earnings/sumoveryear" component={StatisticsEarningsSumOverYear} />
                <Route path="/ukelonn/statistics/earnings/sumovermonth" component={StatisticsEarningsSumOverMonth} />
                <Route path="/ukelonn/statistics" component={Statistics} />
                <Route path="/ukelonn/admin/jobtypes/modify" component={AdminJobtypesModify} />
                <Route path="/ukelonn/admin/jobtypes/create" component={AdminJobtypesCreate} />
                <Route path="/ukelonn/admin/jobtypes" component={AdminJobtypes} />
                <Route path="/ukelonn/admin/jobs/delete" component={AdminJobsDelete} />
                <Route path="/ukelonn/admin/jobs/edit" component={AdminJobsEdit} />
                <Route path="/ukelonn/admin/paymenttypes/modify" component={AdminPaymenttypesModify} />
                <Route path="/ukelonn/admin/paymenttypes/create" component={AdminPaymenttypesCreate} />
                <Route path="/ukelonn/admin/paymenttypes" component={AdminPaymenttypes} />
                <Route path="/ukelonn/admin/users/password" component={AdminUsersChangePassword} />
                <Route path="/ukelonn/admin/users/modify" component={AdminUsersModify} />
                <Route path="/ukelonn/admin/users/create" component={AdminUsersCreate} />
                <Route path="/ukelonn/admin/users" component={AdminUsers} />
                <Route path="/ukelonn/admin/bonuses/modify" component={AdminBonusModify} />
                <Route path="/ukelonn/admin/bonuses/create" component={AdminBonusCreate} />
                <Route path="/ukelonn/admin/bonuses/delete" component={AdminBonusDelete} />
                <Route path="/ukelonn/admin/bonuses" component={AdminBonuses} />
                <Route path="/ukelonn/admin" component={Admin} />
            </Switch>
        </Router>
    );
}

export default App;

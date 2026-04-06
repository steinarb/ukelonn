import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export const api = createApi({
    reducerPath: 'api',
    baseQuery: (...args) => {
        const api = args[1];
        const basename = api.getState().basename;
        return fetchBaseQuery({ baseUrl: basename + '/api' })(...args);
    },
    endpoints: (builder) => ({
        getLogin: builder.query({ query: () => '/login' }),
        getDefaultlocale: builder.query({ query: () => '/defaultlocale' }),
        getAvailablelocales: builder.query({ query: () => '/availablelocales' }),
        getDisplaytexts: builder.query({ query: locale => '/displaytexts?locale=' + locale }),
        getAccount: builder.query({ query: username => '/account/' + username, providesTags: ['PaymentMade', 'JobEdit'] }),
        getJobtypes: builder.query({ query: () => '/jobtypes' }),
        getActivebonuses: builder.query({ query: () => '/activebonuses' }),
        getJobs: builder.infiniteQuery({
            infiniteQueryOptions: {
                initialPageParam: 0,
                getNextPageParam: (lastPage, allPages, lastPageParam, allPageParams) => lastPageParam + 1,
            },
            query: ({ queryArg, pageParam }) => '/jobs/' + queryArg.toString() + '?pagenumber=' + pageParam, providesTags: ['JobRegistered', 'PaymentRegistered', 'JobEdit', 'JobsDelete']
        }),
        getPayments: builder.infiniteQuery({
            infiniteQueryOptions: {
                initialPageParam: 0,
                getNextPageParam: (lastPage, allPages, lastPageParam, allPageParams) => lastPageParam + 1,
            },
            query: ({ queryArg, pageParam }) => '/payments/' + queryArg.toString() + '?pagenumber=' + pageParam, providesTags: ['PaymentRegistered']
        }),
        getSumoveryear: builder.query({ query: username => '/statistics/earnings/sumoveryear/' + username, providesTags: ['JobRegistered', 'JobEdit'] }),
        getSumovermonth: builder.query({ query: username => '/statistics/earnings/sumovermonth/' + username, providesTags: ['JobRegistered', 'JobEdit'] }),
        getAccounts: builder.query({ query: () => '/accounts' }),
        getPaymenttypes: builder.query({ query: () => '/paymenttypes' }),
        getUsers: builder.query({ query: () => '/users' }),
        getAllbonuses: builder.query({ query: () => '/allbonuses' }),
        getNotification: builder.query({ query: username => '/notificationsto/' + username }),
        postUserAdminstatus: builder.query({ query: body => ({ url: '/admin/user/adminstatus', method: 'POST', body }) }),
        postLogin: builder.mutation({
            query: body => ({ url: '/login', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: loginstateAfterLogin } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getLogin',  undefined, () => loginstateAfterLogin));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postLogout: builder.mutation({
            query: body => ({ url: '/logout', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: loginstateAfterLogout } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getLogin',  undefined, () => loginstateAfterLogout));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postJobRegister: builder.mutation({
            query: body => ({ url: '/job/register', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: accountAfterJobRegister } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getAccount',  body.account.username, () => accountAfterJobRegister));
                } catch {
                    // Skip and continue
                }
            },
            invalidatesTags: ['JobRegistered'],
        }),
        postPaymentRegister: builder.mutation({ query: body => ({ url: '/registerpayment', method: 'POST', body }), invalidatesTags: ['PaymentRegistered'] }),
        postNotificationTo: builder.mutation({ query: ({ username, notification }) => ({ url: '/notificationto/' + username, method: 'POST', body: notification }) }),
        postJobtypeModify: builder.mutation({
            query: body => ({ url: '/admin/jobtype/modify', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: jobtypesAfterJobtypeModify } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getJobtypes',  undefined, () => jobtypesAfterJobtypeModify));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postJobtypeCreate: builder.mutation({
            query: body => ({ url: '/admin/jobtype/create', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: jobtypesAfterJobtypeCreate } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getJobtypes',  undefined, () => jobtypesAfterJobtypeCreate));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postJobsDelete: builder.mutation({
            query: body => ({ url: '/admin/jobs/delete', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: jobsAfterJobsDelete } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getJobs',  body.account.accountId, () => ({ pages: [jobsAfterJobsDelete], pageParams: [0] })));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postJobUpdate: builder.mutation({
            query: body => ({ url: '/job/update', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: jobsAfterJobsUpdate } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getJobs',  body.accountId, () => ({ pages: [jobsAfterJobsUpdate], pageParams: [0] })));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postPaymenttypeModify: builder.mutation({
            query: body => ({ url: '/admin/paymenttype/modify', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: paymenttypesAfterPaymenttypeModify } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getPaymenttypes',  undefined, () => paymenttypesAfterPaymenttypeModify));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postPaymenttypeCreate: builder.mutation({
            query: body => ({ url: '/admin/paymenttype/create', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: paymenttypesAfterPaymenttypeCreate } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getPaymenttypes',  undefined, () => paymenttypesAfterPaymenttypeCreate));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postUserModify: builder.mutation({
            query: body => ({ url: '/admin/user/modify', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: usersAfterUserModify } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getUsers', undefined, () => usersAfterUserModify));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postUserChangeadminstatus: builder.mutation({ query: body => ({ url: '/admin/user/changeadminstatus', method: 'POST', body }) }),
        postUserPassword: builder.mutation({
            query: body => ({ url: '/admin/user/password', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: usersAfterUserPassword } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getUsers', undefined, () => usersAfterUserPassword));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postUserCreate: builder.mutation({
            query: body => ({ url: '/admin/user/create', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: usersAfterUserCreate } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getUsers', undefined, () => usersAfterUserCreate));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postModifybonus: builder.mutation({
            query: body => ({ url: '/admin/modifybonus', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: bonusesAfterModifybonus } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getAllbonuses', undefined, () => bonusesAfterModifybonus));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postCreatebonus: builder.mutation({
            query: body => ({ url: '/admin/createbonus', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: bonusesAfterCreatebonus } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getAllbonuses', undefined, () => bonusesAfterCreatebonus));
                } catch {
                    // Skip and continue
                }
            },
        }),
        postDeletebonus: builder.mutation({
            query: body => ({ url: '/admin/deletebonus', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: bonusesAfterDeletebonus } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getAllbonuses', undefined, () => bonusesAfterDeletebonus));
                } catch {
                    // Skip and continue
                }
            },
        }),
   }),
});
console.log(api);

export const {
    useGetLoginQuery,
    useGetDefaultlocaleQuery,
    useGetAvailablelocalesQuery,
    useGetDisplaytextsQuery,
    useGetAccountQuery,
    useGetJobtypesQuery,
    useGetActivebonusesQuery,
    useGetJobsInfiniteQuery,
    useGetPaymentsInfiniteQuery,
    useGetSumoveryearQuery,
    useGetSumovermonthQuery,
    useGetAccountsQuery,
    useGetPaymenttypesQuery,
    useGetUsersQuery,
    useGetAllbonusesQuery,
    useGetNotificationQuery,
    usePostUserAdminstatusQuery,
    usePostLoginMutation,
    usePostLogoutMutation,
    usePostJobRegisterMutation,
    usePostPaymentRegisterMutation,
    usePostNotificationToMutation,
    usePostJobtypeModifyMutation,
    usePostJobtypeCreateMutation,
    usePostJobsDeleteMutation,
    usePostJobUpdateMutation,
    usePostPaymenttypeModifyMutation,
    usePostPaymenttypeCreateMutation,
    usePostUserModifyMutation,
    usePostUserChangeadminstatusMutation,
    usePostUserPasswordMutation,
    usePostUserCreateMutation,
    usePostModifybonusMutation,
    usePostCreatebonusMutation,
    usePostDeletebonusMutation,
} = api;

import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostCreatebonusMutation,
} from '../api';
import { setEnabled, setIconurl, setTitle, setDescription, setBonusFactor, setStartDate, setEndDate } from '../reducers/bonusSlice';
import Locale from './Locale';
import Logout from './Logout';

export default function AdminBonusCreate() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const bonus = useSelector(state => state.bonus);
    const startDate = bonus.startDate.split('T')[0];
    const endDate = bonus.endDate.split('T')[0];
    const dispatch = useDispatch();
    const [ postCreatebonus ] = usePostCreatebonusMutation();
    const onCreateBonusClicked = async () => await postCreatebonus(bonus);

    return (
        <div>
            <nav>
                <Link to="/admin/bonuses">
                    &lt;-
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.createNewBonus}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="enabled">{text.activated}</label>
                        <div>
                            <input
                                id="enabled"
                                type="checkbox"
                                checked={bonus.enabled}
                                onChange={e => dispatch(setEnabled(e.target.checked))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="iconurl">{text.iconURL}</label>
                        <div>
                            <input
                                id="iconurl"
                                type="text"
                                value={bonus.iconurl}
                                onChange={e => dispatch(setIconurl(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="title">{text.title}</label>
                        <div>
                            <input
                                id="title"
                                type="text"
                                value={bonus.title}
                                onChange={e => dispatch(setTitle(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="description">{text.description}</label>
                        <div>
                            <input
                                id="description"
                                type="text"
                                value={bonus.description}
                                onChange={e => dispatch(setDescription(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="bonusfactor">{text.bonusFactor}</label>
                        <div>
                            <input
                                id="bonusfactor"
                                type="text"
                                pattern="[0-9]?[.]?[0-9]?[0-9]?"
                                value={bonus.bonusFactor}
                                onChange={e => dispatch(setBonusFactor(e.target.value))} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="startdate">{text.startDate}</label>
                        <div>
                            <input
                                id="startdate"
                                type="date"
                                value={startDate}
                                onChange={e => dispatch(setStartDate(e.target.value))}
                            />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="enddate">{text.endDate}</label>
                        <div>
                            <input
                                id="enddate"
                                type="date"
                                value={endDate}
                                onChange={e => dispatch(setEndDate(e.target.value))}
                            />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button
                                onClick={onCreateBonusClicked}>
                                {text.createNewBonus}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout/>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

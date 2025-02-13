import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostModifybonusMutation,
} from '../api';
import { setEnabled, setIconurl, setTitle, setDescription, setBonusFactor, setStartDate, setEndDate } from '../reducers/bonusSlice';
import { Link } from 'react-router';
import Locale from './Locale';
import Logout from './Logout';
import Bonuses from './Bonuses';

export default function AdminBonusesModify() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const bonus = useSelector(state => state.bonus);
    const bonusStartDate = bonus.startDate.split('T')[0];
    const bonusEndDate = bonus.endDate.split('T')[0];
    const dispatch = useDispatch();
    const [ postModifybonus ] = usePostModifybonusMutation();
    const onSaveModifiedBonusClicked = async () => await postModifybonus(bonus);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <Link className="btn btn-primary" to="/admin/bonuses">
                    <span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.modifyBonuses}</h1>
                <Locale />
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row mb-2">
                        <label htmlFor="bonus" className="col-form-label col-5">{text.chooseBonus}</label>
                        <div className="col-7">
                            <Bonuses />
                        </div>
                    </div>
                    <div className="row">
                        <div className="col">
                            <div className="form-check">
                            <input
                                id="enabled"
                                className="form-check-input"
                                type="checkbox"
                                checked={bonus.enabled}
                                onChange={e => dispatch(setEnabled(e.target.checked))} />
                                <label htmlFor="enabled" className="form-check-label">{text.activated}</label>
                            </div>
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="iconurl" className="col-form-label col-5">{text.iconURL}</label>
                        <div className="col-7">
                            <input
                                id="iconurl"
                                className="form-control"
                                type="text"
                                value={bonus.iconurl}
                                onChange={e => dispatch(setIconurl(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="title" className="col-form-label col-5">{text.title}</label>
                        <div className="col-7">
                            <input
                                id="title"
                                className="form-control"
                                type="text"
                                value={bonus.title}
                                onChange={e => dispatch(setTitle(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="description" className="col-form-label col-5">{text.description}</label>
                        <div className="col-7">
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={bonus.description}
                                onChange={e => dispatch(setDescription(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="bonusfactor" className="col-form-label col-5">{text.bonusFactor}</label>
                        <div className="col-7">
                            <input
                                id="bonusfactor"
                                className="form-control"
                                type="text"
                                pattern="[0-9]?[.]?[0-9]?[0-9]?"
                                value={bonus.bonusFactor}
                                onChange={e => dispatch(setBonusFactor(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="startdate" className="col-form-label col-5">{text.startDate}</label>
                        <div className="col-7">
                            <input
                                id="startdate"
                                className="form-control"
                                type="date"
                                value={bonusStartDate}
                                onChange={e => dispatch(setStartDate(e.target.value))}
                            />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="enddate" className="col-form-label col-5">{text.endDate}</label>
                        <div className="col-7">
                            <input
                                id="enddate"
                                className="form-control"
                                type="date"
                                value={bonusEndDate}
                                onChange={e => dispatch(setEndDate(e.target.value))}
                            />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={onSaveModifiedBonusClicked}>
                                {text.saveChangesToBonus}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout />
        </div>
    );
}

import React from 'react';
import { useSelector } from 'react-redux';
import {
    useGetDefaultlocaleQuery,
    useGetDisplaytextsQuery,
    usePostDeletebonusMutation,
} from '../api';
import { Link } from 'react-router';
import Locale from './Locale';
import Logout from './Logout';
import Bonuses from './Bonuses';

export default function AdminBonusesDelete() {
    const { isSuccess: defaultLocaleIsSuccess } = useGetDefaultlocaleQuery();
    const locale = useSelector(state => state.locale);
    const { data: text = {} } = useGetDisplaytextsQuery(locale, { skip: !defaultLocaleIsSuccess });
    const bonus = useSelector(state => state.bonus);
    const [ postDeletebonus ] = usePostDeletebonusMutation();
    const onDeleteBonusClicked = async () => await postDeletebonus(bonus);

    return (
        <div>
            <nav>
                <Link to="/admin/bonuses">
                    &lt;-
                    &nbsp;
                    {text.administrateBonuses}
                </Link>
                <h1>{text.deleteBonuses}</h1>
                <Locale />
            </nav>

            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <div>
                        <label htmlFor="bonus">{text.chooseBonus}</label>
                        <div>
                            <Bonuses />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="title">{text.title}</label>
                        <div>
                            <input readOnly id="title" type="text" value={bonus.title} />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="description">{text.description}</label>
                        <div>
                            <input readOnly id="description" type="text" value={bonus.description} />
                        </div>
                    </div>
                    <div>
                        <div/>
                        <div>
                            <button
                                onClick={onDeleteBonusClicked}>
                                {text.deleteSelectedBonus}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <br/>
            <Logout />
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

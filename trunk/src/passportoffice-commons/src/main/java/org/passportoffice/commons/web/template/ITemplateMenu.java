package org.passportoffice.commons.web.template;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 22.12.2009 23:57:25
 *
 * Интерфейс для отображения меню модуля в боковой панели шаблона.
 */
public interface ITemplateMenu extends Serializable{
    /**
     * Заголовок меню
     * @param locale Текущая локаль
     * @return Заголовок меню в зависимости от текущей локали
     */
    public String getTitle(Locale locale);

    /**
     * Список ссылок на функциональные страницы модуля
     * @param locale Текущая локаль
     * @return Список ссылок
     */
    public List<ITemplateLink> getTemplateLinks(Locale locale);

    /**
     * Html element's id.
     */
    public String getTagId();
}

package org.passportoffice.commons.web.template;

import org.apache.wicket.Page;

import java.io.Serializable;
import java.util.Locale;
import org.apache.wicket.PageParameters;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 23.12.2009 17:09:34
 *
 * Интерфейс для локализованной ссылки на страницу модуля
 */
public interface ITemplateLink extends Serializable{

    /**
     * Название ссылки в зависимости от текущей локали
     * @param locale Текущая локаль
     * @return Название ссылки
     */
    public String getLabel(Locale locale);

    /**
     * Страница на которую указывает ссылка
     * @return Страница модуля
     */
    public Class<? extends Page> getPage();

    /**
     * Параметры страницы.
     * @return Параметры страницы.
     */
    public PageParameters getParameters();

    /**
     * Должен возвращать идентификатор html тега.
     * @return tag id.
     */
    String getTagId();
}

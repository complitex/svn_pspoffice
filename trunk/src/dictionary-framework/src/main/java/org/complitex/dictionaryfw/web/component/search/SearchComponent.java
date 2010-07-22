package org.complitex.dictionaryfw.web.component.search;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.passportoffice.information.web.component.search;
//
//import java.util.Collections;
//import java.util.List;
//import javax.ejb.EJB;
//import org.apache.wicket.ajax.AjaxRequestTarget;
//import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
//import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
//import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
//import org.apache.wicket.markup.html.WebMarkupContainer;
//import org.apache.wicket.markup.html.panel.Panel;
//import org.apache.wicket.model.IModel;
//import org.apache.wicket.model.PropertyModel;
//import org.apache.wicket.util.string.Strings;
//import org.complitex.dictionaryfw.dao.EntityDAO;
//import org.complitex.dictionaryfw.entity.AbstractNamedEntity;
//import org.complitex.dictionaryfw.entity.Building;
//import org.complitex.dictionaryfw.entity.BuildingAttribute;
//import org.complitex.dictionaryfw.entity.City;
//import org.complitex.dictionaryfw.entity.Country;
//import org.complitex.dictionaryfw.entity.IDisplayView;
//import org.complitex.dictionaryfw.entity.Region;
//import org.complitex.dictionaryfw.entity.Street;
//
///**
// *
// * @author Artem
// */
//public final class SearchComponent extends Panel {
//
//    private static final int AUTO_COMPLETE_SIZE = 10;
//
//    @EJB(name = "EntityDAO")
//    private EntityDAO entityDAO;
//
//    public SearchComponent(String id) {
//        super(id);
//        init();
//    }
//
//    private class TextFieldModelForNamedEntity<T extends AbstractNamedEntity> extends AutoCompleteTextField.AutoCompleteTextFieldModel<T> {
//
//        public TextFieldModelForNamedEntity(IModel<T> model) {
//            super(model);
//        }
//
//        @Override
//        public String getTextValue(AbstractNamedEntity entity) {
//            return entity.getLocalizedName();
//        }
//    }
//
//    private class SearchPanelUpdater extends AjaxFormComponentUpdatingBehavior {
//
//        public SearchPanelUpdater() {
//            super("onblur");
//        }
//
//        @Override
//        protected void onUpdate(AjaxRequestTarget target) {
//            //update model
//        }
//    }
//
//    private void init() {
//        final Address address = new Address();
//
//        AbstractAutoCompleteTextRenderer<IDisplayView> rendererForEntitiesWithLocalizedName = new AbstractAutoCompleteTextRenderer<IDisplayView>() {
//
//            @Override
//            protected String getTextValue(IDisplayView entity) {
//                return entity.getLocalizedName();
//            }
//        };
//
//        final WebMarkupContainer searchPanel = new WebMarkupContainer("searchPanel");
//        searchPanel.setOutputMarkupId(true);
//
//        AutoCompleteSettings settings = new AutoCompleteSettings();
//        settings.setAdjustInputWidth(false);
//
//        //country
//        AutoCompleteTextField<Country> country = new AutoCompleteTextField<Country>("country",
//                new TextFieldModelForNamedEntity<Country>(new PropertyModel<Country>(address, "country")),
//                rendererForEntitiesWithLocalizedName, settings) {
//
//            @Override
//            protected List<Country> getChoiceList(String searchTextInput) {
//                Country example = new Country();
//                example.setLocalizedName(searchTextInput);
//                List<Country> choiceList = entityDAO.find(example, 0, AUTO_COMPLETE_SIZE, getLocale(), "localizedName", true);
//                return choiceList;
//            }
////            @Override
////            protected String getChoiceValue(Country choice) throws Throwable {
////                return choice.getLocalizedName();
////            }
//        };
//        country.add(new SearchPanelUpdater());
//        searchPanel.add(country);
//
//        //region
//        AutoCompleteTextField<Region> region = new AutoCompleteTextField<Region>("region",
//                new TextFieldModelForNamedEntity<Region>(new PropertyModel<Region>(address, "region")),
//                rendererForEntitiesWithLocalizedName, settings) {
//
//            @Override
//            protected List<Region> getChoiceList(String searchTextInput) {
//                Region example = new Region();
//                if (address.getCountry() != null) {
//                    example.setParent(address.getCountry());
//                }
//                if (example.getParent() != null) {
//                    example.setLocalizedName(searchTextInput);
//                    List<Region> choiceList = entityDAO.find(example, 0, AUTO_COMPLETE_SIZE, getLocale(), "localizedName", true);
//                    choiceList.add(0, Address.NO_REGION);
//                    return choiceList;
//                }
//                return Collections.emptyList();
//            }
////            @Override
////            protected String getChoiceValue(Region choice) throws Throwable {
////                return choice.getLocalizedName();
////            }
//        };
//        region.add(new SearchPanelUpdater());
//        searchPanel.add(region);
//
//        //city
//        AutoCompleteTextField<City> city = new AutoCompleteTextField<City>("city",
//                new TextFieldModelForNamedEntity<City>(new PropertyModel<City>(address, "city")),
//                rendererForEntitiesWithLocalizedName, settings) {
//
//            @Override
//            protected List<City> getChoiceList(String searchTextInput) {
//                City example = new City();
//                if (address.getRegion() != null && !address.getRegion().getId().equals(Address.NO_REGION.getId())) {
//                    example.setParent(address.getRegion());
//                } else if (address.getCountry() != null) {
//                    example.setParent(address.getCountry());
//                }
//                if (example.getParent() != null) {
//                    example.setLocalizedName(searchTextInput);
//                    List<City> choiceList = entityDAO.find(example, 0, AUTO_COMPLETE_SIZE, getLocale(), "localizedName", true);
//                    choiceList.add(0, Address.NO_CITY);
//                    return choiceList;
//                }
//                return Collections.emptyList();
//            }
////            @Override
////            protected String getChoiceValue(City choice) throws Throwable {
////                return choice.getLocalizedName();
////            }
//        };
//        city.add(new SearchPanelUpdater());
//        searchPanel.add(city);
//
//        //street
//        AutoCompleteTextField<Street> street = new AutoCompleteTextField<Street>("street",
//                new TextFieldModelForNamedEntity<Street>(new PropertyModel<Street>(address, "street")),
//                rendererForEntitiesWithLocalizedName, settings) {
//
//            @Override
//            protected List<Street> getChoiceList(String searchTextInput) {
//                Street example = new Street();
//                if (address.getCity() != null && !address.getCity().getId().equals(Address.NO_CITY.getId())) {
//                    example.setParent(address.getCity());
//                }
//                if (example.getParent() != null) {
//                    example.setLocalizedName(searchTextInput);
//                    List<Street> choiceList = entityDAO.find(example, 0, AUTO_COMPLETE_SIZE, getLocale(), "localizedName", true);
//                    choiceList.add(0, Address.NO_STREET);
//                    return choiceList;
//                }
//                return Collections.emptyList();
//            }
////            @Override
////            protected String getChoiceValue(Street choice) throws Throwable {
////                return choice.getLocalizedName();
////            }
//        };
//        street.add(new SearchPanelUpdater());
//        searchPanel.add(street);
//
//        //building
//        AutoCompleteTextField.AutoCompleteTextFieldModel<Building> buildingModel =
//                new AutoCompleteTextField.AutoCompleteTextFieldModel<Building>(new PropertyModel<Building>(address, "building")) {
//
//                    @Override
//                    public String getTextValue(Building building) {
//                        StringBuilder textValue = new StringBuilder();
//
//                        BuildingAttribute first = building.getBuildingAttributes().iterator().next();
//                        textValue.append("дом ").append(first.getLocalizedBuildingNumber());
//                        if (!Strings.isEmpty(first.getLocalizedBuildingCorp())) {
//                            textValue.append(", корп. ").append(first.getLocalizedBuildingCorp());
//                        }
//                        if (!Strings.isEmpty(first.getLocalizedBuildingStructure())) {
//                            textValue.append(", строение ").append(first.getLocalizedBuildingStructure());
//                        }
//
//                        return textValue.toString();
//                    }
//                };
//
//        AutoCompleteTextField<Building> building = new AutoCompleteTextField<Building>("building", buildingModel,
//                rendererForEntitiesWithLocalizedName, settings) {
//
//            @Override
//            protected List<Building> getChoiceList(String searchTextInput) {
//                Building example = new Building();
//                BuildingAttribute attribute = new BuildingAttribute();
//                example.addBuildingAttribute(attribute);
//
//                if (address.getStreet() != null && !address.getStreet().getId().equals(Address.NO_STREET.getId())) {
//                    attribute.setParent(address.getStreet());
//                }
//                attribute.setLocalizedBuildingCorp(searchTextInput);
//                attribute.setLocalizedBuildingNumber(searchTextInput);
//                attribute.setLocalizedBuildingStructure(searchTextInput);
//
//                List<Building> choiceList = entityDAO.find(example, 0, AUTO_COMPLETE_SIZE, getLocale(), "id", true);
//                return choiceList;
//            }
//        };
//        building.add(new SearchPanelUpdater());
//        searchPanel.add(building);
//
//        add(searchPanel);
//    }
//}

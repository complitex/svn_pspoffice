///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.complitex.pspoffice.person.strategy.web.edit.validate;
//
//import com.google.common.collect.ImmutableList;
//import java.text.MessageFormat;
//import org.apache.wicket.Component;
//import org.complitex.dictionary.entity.DomainObject;
//import org.complitex.dictionary.strategy.web.DomainObjectEditPanel;
//import org.complitex.dictionary.strategy.web.validate.IValidator;
//import org.complitex.dictionary.util.EjbBeanLocator;
//import org.complitex.dictionary.web.component.search.SearchComponent;
//import org.complitex.dictionary.web.component.search.SearchComponentState;
//import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
//import org.complitex.pspoffice.person.strategy.web.edit.RegistrationEditComponent;
//
///**
// *
// * @author Artem
// */
//public class RegistrationValidator implements IValidator {
//
//    private RegistrationEditComponent editComponent;
//
//    @Override
//    public boolean validate(DomainObject registration, DomainObjectEditPanel editPanel) {
//        if (editComponent == null) {
//            findEditComponent(editPanel);
//        }
//        return //validateArrivalAddress(editPanel) &&
//                validateCurrentAddress(editPanel);
//    }
//
////    private boolean validateArrivalAddress(DomainObjectEditPanel editPanel) {
////        if (editComponent.isSimpleArrivalAddressText()) {
////            if (editComponent.isArrivalAddressTextEmpty()) {
////                error("arrival_address_empty", editPanel);
////                return false;
////            }
////        } else {
////            return validateArrivalCompositeAddress(editPanel);
////        }
////        return true;
////    }
//
////    private boolean validateArrivalCompositeAddress(DomainObjectEditPanel editPanel) {
////        SearchComponentState arrivalAddressComponentState = editComponent.getArrivalSearchComponentState();
////        DomainObject building = arrivalAddressComponentState.get("building");
////        if (building == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
////            error("arrival_address_failing", editPanel);
////            return false;
////        } else {
////            DomainObject apartment = arrivalAddressComponentState.get("apartment");
////            if (apartment == null || apartment.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
////                return validateOrphanBuilding(building.getId(), "arrival_address_failing", editPanel);
////            } else {
////                DomainObject room = arrivalAddressComponentState.get("room");
////                if (room == null || room.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
////                    return validateOrphanApartment(apartment.getId(), "arrival_address_failing", editPanel);
////                }
////            }
////        }
////        return true;
////    }
//
//    private boolean validateOrphanBuilding(long buildingId, String errorMessage, Component comp) {
//        if (!getStrategy().validateOrphans(buildingId, "building")) {
//            error(errorMessage, comp);
//            return false;
//        }
//        return true;
//    }
//
//    private boolean validateOrphanApartment(long apartmentId, String errorMessage, Component comp) {
//        if (!getStrategy().validateOrphans(apartmentId, "apartment")) {
//            error(errorMessage, comp);
//            return false;
//        }
//        return true;
//    }
//
//    private boolean isTheSameAddress(SearchComponentState address1, SearchComponentState address2) {
//        return address1.isEqual(address2, ImmutableList.of("city", "building", "street", "apartment", "room"));
//    }
//
//    private boolean validateCurrentAddress(DomainObjectEditPanel editPanel) {
//        SearchComponentState addressComponentState = editComponent.getAddressSearchComponentState();
//        DomainObject building = addressComponentState.get("building");
//        if (building == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
//            error("address_failing", editPanel);
//            return false;
//        } else {
//            DomainObject apartment = addressComponentState.get("apartment");
//            if (apartment == null || apartment.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
//                if (!validateOrphanBuilding(building.getId(), "address_failing", editPanel)) {
//                    return false;
//                }
//            } else {
//                DomainObject room = addressComponentState.get("room");
//                if (room == null || room.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
//                    if (!validateOrphanApartment(apartment.getId(), "address_failing", editPanel)) {
//                        return false;
//                    }
//                }
//            }
//        }
//        return true;
////        if (!editComponent.isSimpleArrivalAddressText()) {
////            SearchComponentState arrivalAddressComponentState = editComponent.getArrivalSearchComponentState();
////            if (isTheSameAddress(addressComponentState, arrivalAddressComponentState)) {
////                error("arrival_address_equal_current_address", editPanel);
////                return false;
////            } else {
////                return true;
////            }
////        } else return true;
//    }
//
////    public boolean validateDepartureAddress(DomainObject registration, DomainObjectEditPanel editPanel) {
////        if (editComponent == null) {
////            findEditComponent(editPanel);
////        }
////
////        if (editComponent.isSimpleDepartureAddressText()) {
////            if (editComponent.isDepartureAddressTextEmpty()) {
////                error("departure_address_empty", editPanel);
////                return false;
////            }
////        } else {
////            return validateDepartureCompositeAddress(editPanel);
////        }
////        return true;
////    }
//
////    private boolean validateDepartureCompositeAddress(DomainObjectEditPanel editPanel) {
////        SearchComponentState departureAddressComponentState = editComponent.getDepartureSearchComponentState();
////        DomainObject building = departureAddressComponentState.get("building");
////        if (building == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
////            error("departure_address_failing", editPanel);
////            return false;
////        } else {
////            DomainObject apartment = departureAddressComponentState.get("apartment");
////            if (apartment == null || apartment.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
////                if (!validateOrphanBuilding(building.getId(), "address_failing", editPanel)) {
////                    return false;
////                }
////            } else {
////                DomainObject room = departureAddressComponentState.get("room");
////                if (room == null || room.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
////                    if (!validateOrphanApartment(apartment.getId(), "address_failing", editPanel)) {
////                        return false;
////                    }
////                }
////            }
////        }
////        SearchComponentState currentAddressComponentState = editComponent.getAddressSearchComponentState();
////        if (isTheSameAddress(departureAddressComponentState, currentAddressComponentState)) {
////            error("departure_address_equal_current_address", editPanel);
////            return false;
////        }
////        return true;
////    }
//
//    private void error(String key, Component component, Object... formatArguments) {
//        if (formatArguments == null) {
//            component.error(editComponent.getString(key));
//        } else {
//            component.error(MessageFormat.format(editComponent.getString(key), formatArguments));
//        }
//    }
//
//    private void findEditComponent(DomainObjectEditPanel editPanel) {
//        if (editComponent == null) {
//            editPanel.visitChildren(RegistrationEditComponent.class, new Component.IVisitor<RegistrationEditComponent>() {
//
//                @Override
//                public Object component(RegistrationEditComponent comp) {
//                    editComponent = comp;
//                    return STOP_TRAVERSAL;
//                }
//            });
//        }
//    }
//
//    private RegistrationStrategy getStrategy() {
//        return EjbBeanLocator.getBean(RegistrationStrategy.class);
//    }
//}

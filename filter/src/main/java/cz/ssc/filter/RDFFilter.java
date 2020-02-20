package cz.ssc.filter;

import java.util.Arrays;
import java.util.Collection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Juraj
 */
public class RDFFilter {

    public static boolean filter(Node node, AccessLevel accLvl) throws TagNotFoundException {

        char userAccessLvl = accLvl.getKey();
        Boolean decision = false;
        Element element;
        String keyword;
        char nodeAccessLvl;
        char nodeState;
        if ((node != null) && (node.getNodeType() == Node.ELEMENT_NODE)) {
            element = (Element) node;
            keyword = element.getTagName();
        } else {
            throw new TagNotFoundException("Node not of element type or null");  
        }
        
        switch (keyword) {
            case Constants.EVENT:
            case Constants.SITE:
            case Constants.PIAN:
            case Constants.DOC_UNIT:
                nodeAccessLvl = getNodeAccessLevel(element);
                String parentKeyword;
                // I am filtering dok_jenotka keywords based on required state of its parent keyword
                if(Constants.DOC_UNIT.equals(keyword)){
                    parentKeyword = findDocUnitParent(element);
                    nodeState = getNodeState(keyword, element, parentKeyword);
                    keyword = parentKeyword;
                } else {
                    nodeState = getNodeState(keyword, element, null);
                }
                // I am considering access level (pristupnost) and state (stav)
                if (userAccessLvl >= nodeAccessLvl) {
                  if ((userAccessLvl >= 'C') ||
                         hasReqState(nodeState, keyword)){
                      decision = true;
                  }
                }
                break;
            case Constants.DOCUMENT:
            case Constants.FILE:
            case Constants.PROJECT:
                // Here I consider only state (stav)
                nodeState = getNodeState(keyword, element, null);
                if((userAccessLvl >= 'C') ||
                       hasReqState(nodeState, keyword)){
                    decision = true;
                }
                break;
                // Here I dont filter at all
            case Constants.PAS:
                 nodeAccessLvl = getNodeAccessLevel(element);
                 if (userAccessLvl < nodeAccessLvl) {
                     deleteElementByName(element, "lokalizace");
                     deleteElementByName(element, "katastr");
                 }
            case Constants.EXT_SOURCE:
            case Constants.FLIGHT:
                decision = true;
                break;
            default:
                throw new TagNotFoundException("Unexpected tag " + keyword);  
                
        }
        
        return decision;
    }
        
    public static Boolean hasReqState(Character nodeState, String keyword) throws TagNotFoundException{
        
        switch(keyword){
            case Constants.EVENT:
                Collection<Character> allowedStates = Arrays.asList('4', '5', '8');
                return allowedStates.contains(nodeState);
            case Constants.SITE:
            case Constants.DOCUMENT:
            case Constants.FILE:
                return '3'==nodeState;
            case Constants.PIAN:
                return 'P'==nodeState;
            case Constants.PROJECT:
                return '6'==nodeState;
            case Constants.PAS:
                return '5'==nodeState;
            default:
                throw new TagNotFoundException("Required states for keyword " + keyword + " not defined");
        }
    }
    
    public static Character getNodeState(String keyword, Element element, String parent) throws TagNotFoundException {
        
        switch (keyword) {
            case Constants.PIAN: // In PIAN case I look for tag "ident_cely" not "stav" and it must start with P or N
                Character value = getTagCharValue(element, "ident_cely");
                if ('N' == value || 'P' == value) {
                    return value;
                } else {
                    throw new TagNotFoundException("First characer of indent_cely value is different then P or N");
                }
            case Constants.DOC_UNIT: // Here I am looking for tag "dok_jednotka" there can be "lokalita_stav" and "akce_stav"
                return getTagCharValue(element, parent+"_stav");
            default:
                return getTagCharValue(element, "stav"); // Else I am looking for tag "stav"
        }
    }

    private static String findDocUnitParent(Element element) throws TagNotFoundException {
        NodeList siteState = element.getElementsByTagName("lokalita_stav");
        NodeList eventState = element.getElementsByTagName("akce_stav");
        String parentFound = "";
        if (siteState.getLength() != 0) {
            parentFound = "lokalita";
        } else if (eventState.getLength() != 0) {
            parentFound = "akce";
        } else {
            throw new TagNotFoundException("tag lokalita_stav nor akce_stav was not found");
        }
        return parentFound;
    }
    
    public static Character getNodeAccessLevel(Element element) throws TagNotFoundException {
        Character pristupnost = getTagCharValue(element, "pristupnost");
        // Pristupnost must have value A-E
        if(pristupnost<'A' || pristupnost>'E'){
            throw new TagNotFoundException("Pristupnost tag must have values A-E, value " + pristupnost + " found");
        }
        return pristupnost;
    }
    
    public static void deleteElementByName(Element element, String tag) throws TagNotFoundException {
        NodeList nodeList = element.getElementsByTagName(tag);
        // There must be one tag exactly in the node
        if(nodeList.getLength() != 1){
            throw new TagNotFoundException(tag+" tag not found or found more than "
                    + "one occurance in node " + element.getTextContent());
        }
        nodeList.item(0).getParentNode().removeChild(nodeList.item(0));
    }

    private static Character getTagCharValue(Element element, String tag) throws TagNotFoundException {
        NodeList nodeList = element.getElementsByTagName(tag);
        // There must be one tag exactly in the node
        if(nodeList.getLength() != 1){
            throw new TagNotFoundException(tag+" tag not found or found more than "
                    + "one occurance in node " + element.getTextContent());
        }
        
        // Its value must be one character exactly
        String value = nodeList.item(0).getTextContent();
        if (value.isEmpty()) {
            throw new TagNotFoundException("Empty tag " +tag+ " or unexpected value "
                    + "length (expecting one character) in node " + element.getTextContent());
        }
        
        return value.toCharArray()[0];
    }

}

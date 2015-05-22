/******************************************************************************
 * Product: iDempiere Free ERP Project based on Compiere (2006)               *
 * Copyright (C) 2014 Redhuan D. Oon All Rights Reserved.                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *  FOR NON-COMMERCIAL DEVELOPER USE ONLY                                     *
 *  @author Redhuan D. Oon  - red1@red1.org  www.red1.org                     *
 *****************************************************************************/

package org.idempiere.component;

import java.sql.Timestamp;
import java.util.List;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.base.event.LoginEventData;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MRequest;
import org.compiere.model.MRequestType;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.wf.MWorkflow;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductPlanning;
import org.osgi.service.event.Event;

/**
 *  @author red1
 */
public class PluginDocEvent extends AbstractEventHandler{
	private static CLogger log = CLogger.getCLogger(PluginDocEvent.class);
	private String trxName = "";
	private PO po = null;
	private String m_processMsg = "";
	@Override
	protected void initialize() { 
	//register EventTopics and TableNames 
		registerTableEvent(IEventTopics.DOC_AFTER_COMPLETE, MOrder.Table_Name);
		log.info("<PLUGIN> REQUEST CALENDAR IS NOW INITIALIZED");
		}

	@Override
	protected void doHandleEvent(Event event) {
		String type = event.getTopic();
		//testing that it works at login
		if (type.equals(IEventTopics.AFTER_LOGIN)) {
			LoginEventData eventData = getEventData(event);
			log.fine(" topic="+event.getTopic()+" AD_Client_ID="+eventData.getAD_Client_ID()
					+" AD_Org_ID="+eventData.getAD_Org_ID()+" AD_Role_ID="+eventData.getAD_Role_ID()
					+" AD_User_ID="+eventData.getAD_User_ID());
			}
		else 
		{
			setPo(getPO(event));
			setTrxName(po.get_TrxName());
			log.info(" topic="+event.getTopic()+" po="+po);
			if (po instanceof MOrder){
				if (IEventTopics.DOC_AFTER_COMPLETE == type){
					
					//get the Sales Order document
					MOrder order = (MOrder)po;
					setTrxName(trxName);
					//get the DatePromised 
					Timestamp dateComplete = order.getDatePromised()==null?order.getDateOrdered():order.getDatePromised();
					Timestamp dateStart = order.getDateOrdered();
					
					//create new Request event for the Calendar to display
					MRequest request = new MRequest(Env.getCtx(),0,trxName);
					request.setC_Order_ID(order.getC_Order_ID());
					request.setDateStartPlan(dateStart);
					request.setDateCompletePlan(dateComplete);
					
					//Summary Information that will be displayed on the Dashboard Calendar
					//get Product Info and Location to Ship to
					MOrderLine lines[] = order.getLines();
					StringBuilder buffer = new StringBuilder(order.getC_BPartner().getName()+" "+order.getC_Currency().getCurSymbol()+
							order.getGrandTotal().toString()+" - ");
					for (MOrderLine line:lines){
						buffer = buffer.append(line.getQtyOrdered()+" "+line.getM_Product().getName()+" ");
					}
					//
					MBPartner partner = (MBPartner) order.getC_BPartner();
					MBPartnerLocation[] locations = partner.getLocations(false);
					for (MBPartnerLocation loc:locations){
						if (loc.isShipTo())
							buffer = buffer.append("Ship to:"+loc.getC_Location().getAddress1()+","+loc.getC_SalesRegion().getName());
					}
					
					request.setSummary(buffer.toString());
					
					MRequestType rt = new Query(Env.getCtx(),MRequestType.Table_Name,"Name='Service Request'",null).first();
					request.setR_RequestType_ID(rt.get_ID());
					request.setSalesRep_ID(order.getSalesRep_ID());
					request.saveEx(trxName);
					log.info("Creating new Request "+request.get_ID());
					//Assign DocType for CustomerReturn 
					
					log.info("REQUEST for Calendar created from Order: "+order.getDocumentNo());
				}
			}
		}
	}
	private void setPo(PO eventPO) {
		 po = eventPO;
	}

	private void setTrxName(String get_TrxName) {
		trxName = get_TrxName;		
	}
}

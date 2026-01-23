package com.a2z.controllers;

import java.io.IOException;

import com.a2z.facades.OrderFacade;
import com.a2z.services.interfaces.MediaService;
import com.a2z.services.interfaces.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.a2z.data.MediaContainerData;
import com.a2z.data.OrderData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@ResponseBody
@RequestMapping("/order")
@Secured("SCOPE_app.read")
@Validated
public class A2zOrderController extends RootController {

	@Autowired
	OrderService orderService;

	@Autowired
	OrderFacade orderFacade;
	
	@Autowired
	MediaService mediaService;
	
	@PostMapping("/submit")
	@ResponseBody
	public OrderData submitOrder(@RequestBody @Valid OrderData orderData, HttpServletRequest request) {
		String userName = getSessionUserName();
		OrderData orderDataNew = new OrderData();
		if(StringUtils.isEmpty(userName)) {
			return orderDataNew;
		}
		orderDataNew = orderFacade.submitOrder(orderData,userName,false,null);
		return orderDataNew;
	}
	
	/*@GetMapping("/all")
	public List<OrderData> getOrders(HttpServletRequest request){
		String userName = getSessionUserName();
		return orderService.getAllOrders(userName);
	}*/

	/*@GetMapping("/{orderId}")
	public OrderData getOrderDetails(@PathVariable @Valid long orderId , HttpServletRequest request) {
		String userName = getSessionUserName();
		return orderService.getOrderDetail(userName, orderId);
	}*/
	
	@GetMapping("/return")
	@ResponseBody
	public OrderData returnOrExtend(@RequestParam @Valid long orderId ,@RequestParam @Valid boolean isReturn
			, @RequestParam @Valid boolean isExtend ,  HttpServletRequest request) {
		String userName = getSessionUserName();
		return orderFacade.returnOrExtend(userName, orderId ,isReturn , isExtend);
	}
	
	@PostMapping("/proof/upload")
	@ResponseBody
	public MediaContainerData uploadProof(@RequestPart("files") MultipartFile[] files, HttpServletRequest request) throws IOException {
		String userName = getSessionUserName();
		if (isSessionValid())
			return mediaService.uploadMedia(userName+"-proofs", files, false);
		return new MediaContainerData();
	}
	@GetMapping("/allTypes")
	@ResponseBody
	public Iterable<String> getAllOrderTypes() {
		return orderFacade.getAllOrderTypes();
	}

	@GetMapping("/allOrderStatuses")
	@ResponseBody
	public Iterable<String> getAllOrderStatuses() {
		return orderFacade.getAllOrderStatuses();
	}


}


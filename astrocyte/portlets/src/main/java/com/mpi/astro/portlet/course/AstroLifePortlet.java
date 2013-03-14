package com.mpi.astro.portlet.course;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.mpi.astro.portlet.BaseAstroPortlet;

@Controller
@RequestMapping("VIEW")
public class AstroLifePortlet extends BaseAstroPortlet {
	@RenderMapping
	public String showDefaultView(RenderRequest request, RenderResponse response) {
		return "astrolife/view";
	}
	
}

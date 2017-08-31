package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Collaborator;
import com.teamtreehouse.instateam.model.Project;
import com.teamtreehouse.instateam.model.Role;
import com.teamtreehouse.instateam.service.CollaboratorService;
import com.teamtreehouse.instateam.service.ProjectService;
import com.teamtreehouse.instateam.service.RoleService;
import com.teamtreehouse.instateam.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private CollaboratorService collaboratorService;

    @Autowired
    private ProjectService projectService;

    //Index of all roles
    @RequestMapping("/roles")
    @SuppressWarnings("unchecked")
    public String listRoles(Model model) {
        // TODO: Get all roles
        List<Role> roles = roleService.findAll();
        if (!model.containsAttribute("role")) {
            model.addAttribute("role", new Role());
        }
        model.addAttribute("roles", roles);
        return "role/index";
    }

    // Add a role
    @RequestMapping(value = "/roles/add", method = RequestMethod.POST)
    public String addRole(@Valid Role role, BindingResult result, RedirectAttributes redirectAttributes) {

        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.role", result);
            redirectAttributes.addFlashAttribute("role", role);

            return "redirect:/roles";
        }
        roleService.save(role);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Role successfully added!", FlashMessage.Status.SUCCESS));

        // TODO: Redirect browser to /roles
        return "redirect:/roles";
    }

    @RequestMapping("/roles/{roleId}/edit")
    public String formEditRole(@PathVariable Long roleId, Model model) {
        // if the model already contains a "role", the form will be pre-populated (from redirection)
        if(!model.containsAttribute("role")) {
            model.addAttribute("role", roleService.findById(roleId));
        }


        return "role/edit";
    }

    @RequestMapping(value = "roles/{roleId}", method = RequestMethod.POST)
    public String updateRole(@Valid Role role, BindingResult result, RedirectAttributes redirectAttributes) {
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.role", result);
            redirectAttributes.addFlashAttribute("role", role);

            return String.format("redirect:/roles/%s/edit",role.getId());
        }

        roleService.save(role);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Role successfully updated!", FlashMessage.Status.SUCCESS));

        return "redirect:/roles";
    }

    @RequestMapping(value = "roles/{roleId}/delete", method = RequestMethod.POST)
    public String deleteRole(@PathVariable Long roleId, RedirectAttributes redirectAttributes) {
        Role role = roleService.findById(roleId);
        List<Collaborator> collaborators = collaboratorService.findAll();
        List<Project> projects = projectService.findAll();

        for (Collaborator collaborator: collaborators) {
            if(collaborator.getRole().getId() == roleId) {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Cannot delete. Role is assigned to a collaborator.", FlashMessage.Status.FAILURE));
                return String.format("redirect:/roles/%s/edit", roleId);
            }
        }

        for (Project project: projects) {
            if(project.getRolesNeeded().contains(role)) {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Cannot delete. Role is needed for a project.", FlashMessage.Status.FAILURE));
                return String.format("redirect:/roles/%s/edit", roleId);
            }
        }

        roleService.delete(role);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Role successfully deleted!", FlashMessage.Status.SUCCESS));

        return "redirect:/roles";
    }

}

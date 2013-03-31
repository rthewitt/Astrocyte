package com.mpi.astro.core.service.edu;

import java.util.Calendar;
import java.util.Locale;

import javax.portlet.PortletPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutConstants;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortletKeys;
import com.mpi.astro.core.model.edu.CourseInstance;
import com.mpi.astro.core.model.edu.Student;
import com.mpi.astro.core.util.PropertiesUtil;

@Service
public class AstroService {
	
	private static final Logger logger = LoggerFactory.getLogger(AstroService.class);
	
	// Crate community
	// Add users to community
	// Create default page
	// Add layout
	// Add portlet with journal entry. - this will be a template blog or something.  
	public void createCommunityForCourseInstance(ThemeDisplay td, Student[] students, CourseInstance course) throws PortalException, SystemException {
		
		logger.debug("Request to create community for " + students.length + " students: " + course.getName());
		
		long currentUserId = td.getUserId();
		
		/*
		Company company = CompanyLocalServiceUtil.getCompanyByMx(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
		long globalGroupId = company.getGroup().getGroupId(); */
		
		long[] roles = {};
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setAddCommunityPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		
		String[] parts = course.getCourseUUID().split("-");
//		String friendlyURL = String.format("/group/%s/%s", parts[0], parts[1]);
		String friendlyURL = "";
		

		Group courseGroup = GroupLocalServiceUtil.addGroup(currentUserId, null, 0L, course.getName(), course.getDescription(), 
				GroupConstants.TYPE_COMMUNITY_RESTRICTED, friendlyURL, true, serviceContext);
		
		logger.debug("Community created : " + courseGroup.getName());
		
		// TODO branch logic to test if user already exists in liferay.
		// TODO bridge databases and users (Astro, Liferay) without losing flexibility
		long[] lrIds = new long[students.length];
		for(int i=0;i<students.length; i++) {
			try{
				// Change currentUserId to a system user that corresponds to "Myelin Admin User" - maybe
				User portalUser = addUser(currentUserId, td.getCompanyId(), students[i], true, "Student", roles);
				logger.debug("User " + portalUser.getFullName() + " added to Portal with id " + portalUser.getUserId());
				lrIds[i] = portalUser.getUserId();
			} catch(Exception se) {
				logger.error("Problem adding student "+students[i].getId()+"to newly created community." +
						"\nUser has already been enrolled in course.", se);
			}
		}
		
		logger.debug("adding users to group");
		// add newly created users to group
		UserLocalServiceUtil.addGroupUsers(courseGroup.getGroupId(), lrIds);
		logger.debug("Users added to group with groupId " + courseGroup.getGroupId());
		// Create a new page layout.
		Layout layout = LayoutLocalServiceUtil.addLayout(currentUserId, //userId
				courseGroup.getGroupId(), //groupId
	             false,  //privateLayout
	             0,        //parentLayoutId
	             "main",    //name
	             "home",    //title
	             course.getDescription(), //description
	             LayoutConstants.TYPE_PORTLET, //type
	             false,        //hidden
	             "/home",    //friendlyURL
	             serviceContext);
	// set Layout
	LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();
//	layoutTypePortlet.setLayoutTemplateId(userId, "my_layout_template_id");
		
		// add a content display portlet
		// The column id and position (last 2 parameters below) will depend on your layout template
		String journalPortletId = layoutTypePortlet.addPortletId(currentUserId,
		                                        PortletKeys.JOURNAL_CONTENT,
		                                        "column-3",
		                                        -1);

		long companyId = td.getCompanyId();
		long ownerId = PortletKeys.PREFS_OWNER_ID_DEFAULT;
		int ownerType = PortletKeys.PREFS_OWNER_TYPE_LAYOUT;

		// Retrieve the portlet preferences for the journal portlet instance just created
		PortletPreferences prefs = PortletPreferencesLocalServiceUtil.getPreferences(companyId,
		                                        ownerId,
		                                        ownerType,
		                                        layout.getPlid(),
		                                        journalPortletId);

		// set desired article id for content display portlet
//		prefs.setValue("article-id", "123456");
//		prefs.setValue("group-id", String.valueOf(courseGroup.getGroupId())); // Content may be from another group

		// update the portlet preferences
		PortletPreferencesLocalServiceUtil.updatePreferences(ownerId, ownerType, layout
		                                        .getPlid(), journalPortletId, prefs);
		
		// update layout
		LayoutLocalServiceUtil.updateLayout(layout.getGroupId(),
                layout.isPrivateLayout(),
                layout.getLayoutId(),
                layout.getTypeSettings());
	}
	
	protected User addUser(long creatorUserId, long companyId, Student student, boolean male, String jobTitle, long[] roleIds) throws PortalException, SystemException {
		return addUser(creatorUserId, companyId, student.getStudentId(), student.getFirstName(), student.getLastName(), male, jobTitle, roleIds);
	}
	
	protected User addUser( long creatorUserId,
            long companyId, String screenName, String firstName,
            String lastName, boolean male, String jobTitle, long[] roleIds) throws PortalException, SystemException {
		
		
//		long creatorUserId = 0;
        boolean autoPassword = false;
        String password1 = screenName;
        String password2 = password1;
        boolean autoScreenName = false;
        String emailAddress = screenName + "@"+PropertiesUtil
        		.getProperty(PropertiesUtil.PROP_DEFAULT_EMAIL_DOMAIN);
        String openId = StringPool.BLANK;
        
        Locale locale = Locale.US;
        String middleName = StringPool.BLANK;
        int prefixId = 0;
        int suffixId = 0;
        int birthdayMonth = Calendar.JANUARY;
        int birthdayDay = 1;
        int birthdayYear = 1970;

        Group guestGroup = GroupLocalServiceUtil.getGroup(
            companyId, GroupConstants.GUEST);

        long[] groupIds = new long[] {guestGroup.getGroupId()};

        Organization mpiOrg =
            OrganizationLocalServiceUtil.getOrganization(
                companyId, "Myelin Price Interactive");

        long[] organizationIds = new long[] {
        		mpiOrg.getOrganizationId()
        };

        long[] userGroupIds = null;
        boolean sendEmail = false;
        ServiceContext serviceContext = null;
        
        long facebookId = 0L; // I added this
        
        logger.debug(String.format("Calling UserLocalServiceUtil.addUser - creatorUserId: %s, companyId: %s, autoPassword: %s, password1: %s, password2: %s, " +
        		"autoScreenName: %s, screenName: %s, emailAddress: %s, facebookId: %s, openId: %s, locale: %s, " +
        		"firstName: %s, middleName: %s, lastName: %s, prefixId: %s, suffixId: %s, " +
        		"male: %s, birthdayMonth: %s, birthdayDay: %s, birthdayYear: %s, jobTitle: %s, groupIds: %s, organizationIds: %s, " +
        		"roleIds, userGroupIds, sendEmail, serviceContext", creatorUserId, companyId, autoPassword, password1, password2, 
        		autoScreenName, screenName, emailAddress, facebookId, openId, locale, 
        		firstName, middleName, lastName, prefixId, suffixId, 
        		male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds, 
        		roleIds, userGroupIds, sendEmail, serviceContext));
        
        return UserLocalServiceUtil.addUser(creatorUserId, companyId, autoPassword, password1, password2, 
        		autoScreenName, screenName, emailAddress, facebookId, openId, locale, 
        		firstName, middleName, lastName, prefixId, suffixId, 
        		male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds, 
        		roleIds, userGroupIds, sendEmail, serviceContext);
	}
	
}
/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cfenv.jdbc;

import org.junit.Test;

import io.pivotal.cfenv.core.UriInfo;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mark Pollack
 * @author David Turanski
 */
public class SqlServerJdbcTests extends AbstractJdbcTests {

	private static final String INSTANCE_NAME = "database";
	private static final String SERVICE_NAME = "sqlserver-ups";


	@Test
	public void sqlServerServiceCreation() {
		mockVcapServices(getServicesPayload(
				getSqlserverUserProvidedServicePayload(SERVICE_NAME, hostname, port, username, password, INSTANCE_NAME)
		));

		CfJdbcEnv cfJdbcEnv = new CfJdbcEnv();
		CfJdbcService cfJdbcService = cfJdbcEnv.findJdbcServiceByName(SERVICE_NAME);
		assertThat(cfJdbcService.getDriverClassName()).isEqualTo("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		String jdbcUrl = cfJdbcEnv.findJdbcServiceByName(SERVICE_NAME).getJdbcUrl();
		String expectedJdbcUrl = getExpectedJdbcUrl(SqlServerJdbcUrlCreator.SQLSERVER_SCHEME, INSTANCE_NAME);
		assertThat(jdbcUrl).isEqualTo(expectedJdbcUrl);
	}

	@Test
	public void sqlServerServiceCreationWithSpecialChars() {
		String userWithSpecialChars = "u%u:u+";
		String passwordWithSpecialChars = "p%p:p+";

		mockVcapServices(getServicesPayload(
				getSqlserverUserProvidedServicePayload(SERVICE_NAME, hostname, port, userWithSpecialChars,
						passwordWithSpecialChars, INSTANCE_NAME)
		));

		CfJdbcEnv cfJdbcEnv = new CfJdbcEnv();
		CfJdbcService cfJdbcService = cfJdbcEnv.findJdbcServiceByName(SERVICE_NAME);

		String jdbcUrl = cfJdbcService.getJdbcUrl();
		String expectedJdbcUrl = getExpectedJdbcUrl(SqlServerJdbcUrlCreator.SQLSERVER_SCHEME, INSTANCE_NAME,
				userWithSpecialChars, passwordWithSpecialChars);
		assertThat(expectedJdbcUrl).isEqualTo(jdbcUrl);
	}


	protected String getExpectedJdbcUrl(String scheme, String name) {
		return String.format("jdbc:%s://%s:%d;database=%s;user=%s;password=%s",
				scheme, hostname, port, name, UriInfo.urlEncode(username), UriInfo.urlEncode(password));
	}

	protected String getExpectedJdbcUrl(String scheme, String name, String uname, String pwd) {
		return String.format("jdbc:%s://%s:%d;database=%s;user=%s;password=%s",
				scheme, hostname, port, name, UriInfo.urlEncode(uname), UriInfo.urlEncode(pwd));
	}

	private String getSqlserverUserProvidedServicePayload(String serviceName, String hostname, int port,
			String user, String password, String name) {
		return getTemplatedPayload("test-sqlserver-info.json", serviceName,
				hostname, port, user, password, name);
	}
}

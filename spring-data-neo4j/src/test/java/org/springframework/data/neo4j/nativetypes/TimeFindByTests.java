/*
 * Copyright (c)  [2011-2018] "Pivotal Software, Inc." / "Neo Technology" / "Graph Aware Ltd."
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with
 * separate copyright notices and license terms. Your use of the source
 * code for these subcomponents is subject to the terms and
 * conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package org.springframework.data.neo4j.nativetypes;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TimeFindByTests.TimePersistenceContext.class)
@DirtiesContext
public class TimeFindByTests {

	@Autowired
	private TimeDomainRepository repository;

	private TimeDomain timeDomain;
	private Date date;
	private LocalDate localDate;
	private LocalDateTime localDateTime;
	private Period period;
	private TemporalAmount temporalAmount;
	private Duration duration;

	@Before
	public void setUpDomainObject() {
		repository.deleteAll();

		timeDomain = new TimeDomain();

		date = new Date();
		localDate = LocalDate.now();
		localDateTime = LocalDateTime.now();
		period = Period.ofMonths(2);
		temporalAmount = period;
		duration = Duration.ofHours(3);

		timeDomain.setDate(date);
		timeDomain.setLocalDate(localDate);
		timeDomain.setLocalDateTime(localDateTime);

		timeDomain.setPeriod(period);
		timeDomain.setDuration(duration);

		timeDomain.setTemporalAmount(temporalAmount);

		repository.save(timeDomain);
	}

	@Test
	public void findByDate() {
		List<TimeDomain> result = repository.findByDate(date);
		assertThat(result).hasSize(1);
	}

	@Test
	public void findByLocalDate() {
		List<TimeDomain> result = repository.findByLocalDate(localDate);
		assertThat(result).hasSize(1);
	}

	@Test
	public void findByLocalDateTime() {
		List<TimeDomain> result = repository.findByLocalDateTime(localDateTime);
		assertThat(result).hasSize(1);
	}

	@Test
	public void findByPeriod() {
		List<TimeDomain> result = repository.findByPeriod(period);
		assertThat(result).hasSize(1);
	}

	@Test
	public void findByDuration() {
		List<TimeDomain> result = repository.findByDuration(duration);
		assertThat(result).hasSize(1);
	}

	@Test
	public void findByTemporalAmount() {
		List<TimeDomain> result = repository.findByTemporalAmount(temporalAmount);
		assertThat(result).hasSize(1);
	}

	@Configuration
	@EnableNeo4jRepositories
	@EnableTransactionManagement
	static class TimePersistenceContext {

		@Bean
		ServerControls neo4j() {
			return TestServerBuilders.newInProcessBuilder().newServer();
		}

		@Bean
		org.neo4j.ogm.config.Configuration neo4jOGMConfiguration(ServerControls serverControls) {
			return new org.neo4j.ogm.config.Configuration.Builder() //
					.uri(serverControls.boltURI().toString()) //
					.useNativeTypes() //
					.build();
		}

		@Bean
		public SessionFactory sessionFactory(org.neo4j.ogm.config.Configuration configuration) {
			return new SessionFactory(configuration, "org.springframework.data.neo4j.nativetypes");
		}

		@Bean
		public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
			return new Neo4jTransactionManager(sessionFactory);
		}
	}
}

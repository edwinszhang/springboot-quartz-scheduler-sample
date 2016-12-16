package team.pi.sample.scheduler;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import team.pi.sample.scheduler.job.SampleJob;
import team.pi.sample.scheduler.spring.AutowiringSpringBeanJobFactory;


/**
 * Created on 2016/12/8
 * [Function]
 *
 * @author zhangshuai
 */
@Configuration
//@ConditionalOnProperty(name = "quartz.enabled")
public class SchedulerConfig {

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean(name = "job1")
    public JobDetailFactoryBean jobDetailFactoryBean() {
        return createJobDetail(SampleJob.class);
    }

    @Bean(name = "sampleJobTrigger")
    public SimpleTriggerFactoryBean simpleTriggerFactoryBean(
        @Qualifier("job1") JobDetail jobDetail
    ) {
        return createTrigger(jobDetail);
    }

    @Bean(name = "sampleCronTrigger")
    public CronTriggerFactoryBean cronTriggerFactoryBean (
        @Qualifier("job1") JobDetail jobDetail
    ) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(3000);
        factoryBean.setName("cronTrigger");
        factoryBean.setGroup("testGroup");
        factoryBean.setCronExpression("0/10 * * * * ?");
        return factoryBean;
    }

    /**
     * create scheduler bean
     *
     * @return schedulerFactoryBean
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
        JobFactory jobFactory,
        @Qualifier(value = "sampleJobTrigger") Trigger sampleJobTrigger,
        @Qualifier(value = "sampleCronTrigger") Trigger sampleCronTrigger
    ) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        // You can set a series of triggers here
        schedulerFactoryBean.setTriggers(sampleJobTrigger, sampleCronTrigger);
        schedulerFactoryBean.setJobFactory(jobFactory);

        return schedulerFactoryBean;
    }


    /**
     * helper to create JobDetails
     *
     * @param jobDetailClazz class you want to create
     * @return new jobDetail instance
     */
    private JobDetailFactoryBean createJobDetail(Class jobDetailClazz){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobDetailClazz);
        factoryBean.setName("helloJob");
        factoryBean.setGroup("myGroup");
        return factoryBean;
    }

    /**
     * helper to create trigger
     *
     * @param jobDetail
     * @return
     */
    private SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();

        factoryBean.setJobDetail(jobDetail);

        factoryBean.setStartDelay(3000);        // 3s
        factoryBean.setRepeatInterval(3000);    // 3s
        factoryBean.setRepeatCount(5);          // 5 + 1 times

        return factoryBean;
    }

}

 # Hello, spring-boot-jpa.
 
## 开发要点

### @Transactional

- 可添加在Service类上，方法继承。
- 默认回滚`RuntimeException` & `Error`, 不回滚 `Exception`, 可通过`@Transactional(rollbackFor={})`设置
- 可查看`{@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(Class clazz)}`

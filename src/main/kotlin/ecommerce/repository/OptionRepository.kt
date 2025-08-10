package ecommerce.repository

import ecommerce.model.Option
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OptionRepository : JpaRepository<Option, Long>

package org.scalafmt.cli

import org.scalafmt.sysops.AbsoluteFile
import org.scalafmt.sysops.PlatformRunOps

import scala.io.Source

import com.facebook.nailgun.NGContext

private[scalafmt] trait CliUtils {
  def nailMain(nGContext: NGContext): Unit = {
    val workingDirectory = AbsoluteFile
      .fromPathIfAbsolute(nGContext.getWorkingDirectory).getOrElse(
        throw new IllegalStateException(
          s"Expected absolute path, " +
            s"obtained nGContext.getWorkingDirectory = ${nGContext
                .getWorkingDirectory}",
        ),
      )
    import PlatformRunOps.parasiticExecutionContext
    Cli.mainWithOptions(
      CliOptions.default.copy(common =
        CliOptions.default.common.copy(
          cwd = Some(workingDirectory),
          out = nGContext.out,
          in = nGContext.in,
          err = nGContext.err,
        ),
      ),
      nGContext.getArgs: _*,
    ).map(exit => nGContext.exit(exit.code))
  }

  protected def getDynamicRunner: Option[ScalafmtRunner] =
    Some(ScalafmtDynamicRunner)

  def readInputLines: Iterator[String] = Source.stdin.getLines()
}

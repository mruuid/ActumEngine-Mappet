package unkn0wn.ae.api.scripts.user.renderer;

/**
 * Interpolation is used in {@link IScriptCamera}, {@link IScriptHandRender} and {@link IScriptHudRender}.
 * <p>Interpolation creates smooth movements via the Mclib's mod by McHorse.</p>
 *
 * <pre>{@code
 *     linear,
 *
 *     quad_in,
 *     quad_out,
 *     quad_inout,
 *
 *     cubic_in,
 *     cubic_out,
 *     cubic_inout,
 *
 *     exp_in,
 *     exp_out,
 *     exp_inout,
 *
 *     back_in,
 *     back_out,
 *     back_inout,
 *
 *     elastic_in,
 *     elastic_out,
 *     elastic_inout,
 *
 *     bounce_in,
 *     bounce_out,
 *     bounce_inout,
 *
 *     sine_in,
 *     sine_out,
 *     sine_inout,
 *
 *     quart_in,
 *     quart_out,
 *     quart_inout,
 *
 *     quint_in,
 *     quint_out,
 *     quint_inout,
 *
 *     circle_in,
 *     circle_out,
 *     circle_inout.
 * }</pre>
 * */
public interface Interpolation {
    /**
     * Interpolation is used in {@link IScriptCamera}, {@link IScriptHandRender} and {@link IScriptHudRender}.
     * <p>Interpolation creates smooth movements via the Mclib's mod by McHorse.</p>
     *
     * <pre>{@code
     *     linear,
     *
     *     quad_in,
     *     quad_out,
     *     quad_inout,
     *
     *     cubic_in,
     *     cubic_out,
     *     cubic_inout,
     *
     *     exp_in,
     *     exp_out,
     *     exp_inout,
     *
     *     back_in,
     *     back_out,
     *     back_inout,
     *
     *     elastic_in,
     *     elastic_out,
     *     elastic_inout,
     *
     *     bounce_in,
     *     bounce_out,
     *     bounce_inout,
     *
     *     sine_in,
     *     sine_out,
     *     sine_inout,
     *
     *     quart_in,
     *     quart_out,
     *     quart_inout,
     *
     *     quint_in,
     *     quint_out,
     *     quint_inout,
     *
     *     circle_in,
     *     circle_out,
     *     circle_inout.
     * }</pre>
     * */
    void INFO();
}
